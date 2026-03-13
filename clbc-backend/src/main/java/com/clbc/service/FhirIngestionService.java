package com.clbc.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.clbc.model.*;
import com.clbc.repository.*;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Condition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FhirIngestionService {

    private final ResidentRepository residentRepository;
    private final PreferenceRepository preferenceRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ObservationRepository observationRepository;
    private final AllergyRepository allergyRepository;
    private final MedicalConditionRepository medicalConditionRepository;
    private final AuditService auditService;
    private final FhirContext fhirContext = FhirContext.forR4();

    @Transactional
    public void ingestBundle(String jsonPayload) {
        String communityId = TenantContext.getTenantId();
        IParser parser = fhirContext.newJsonParser();
        Bundle bundle = parser.parseResource(Bundle.class, jsonPayload);

        // 1. Process Patient first to get/create Resident
        Patient fhirPatient = bundle.getEntry().stream()
                .map(Bundle.BundleEntryComponent::getResource)
                .filter(r -> r instanceof Patient)
                .map(r -> (Patient) r)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Patient resource found in FHIR bundle"));

        String mrn = extractMrn(fhirPatient);
        Resident resident = residentRepository.findByMrnAndCommunityId(mrn, communityId)
                .orElseGet(() -> createPlaceholderResident(fhirPatient, mrn, communityId));

        // 2. Process other resources in the bundle
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource resource = entry.getResource();
            if (resource instanceof MedicationRequest) {
                processMedication((MedicationRequest) resource, resident);
            } else if (resource instanceof org.hl7.fhir.r4.model.Observation) {
                processObservation((org.hl7.fhir.r4.model.Observation) resource, resident);
            } else if (resource instanceof AllergyIntolerance) {
                processAllergy((AllergyIntolerance) resource, resident);
            } else if (resource instanceof Condition) {
                processCondition((Condition) resource, resident);
            }
        }

        auditService.logAction("FHIR_INGEST", "BUNDLE", resident.getId(),
                "Successfully ingested clinical data from hospital", null);
    }

    private String extractMrn(Patient patient) {
        return patient.getIdentifier().stream()
                .filter(i -> {
                    String typeText = i.getType() != null ? i.getType().getText() : "";
                    return "MRN".equalsIgnoreCase(typeText) || (i.getSystem() != null && i.getSystem().contains("mrn"));
                })
                .map(Identifier::getValue)
                .findFirst()
                .orElse(patient.getIdElement().getIdPart());
    }

    private Resident createPlaceholderResident(Patient patient, String mrn, String communityId) {
        Resident resident = Resident.builder()
                .id(UUID.randomUUID().toString())
                .communityId(communityId)
                .mrn(mrn)
                .firstName(patient.getNameFirstRep().getGivenAsSingleString())
                .lastName(patient.getNameFirstRep().getFamily())
                .gender(patient.getGender() != null ? patient.getGender().toCode() : "unknown")
                .status(Resident.ResidentStatus.RECEIVED)
                .build();

        if (patient.getBirthDate() != null) {
            resident.setDateOfBirth(patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        return residentRepository.save(resident);
    }

    private void processMedication(MedicationRequest req, Resident resident) {
        Prescription prescription = Prescription.builder()
                .residentId(resident.getId())
                .communityId(resident.getCommunityId())
                .medicationName(req.getMedicationCodeableConcept().getText())
                .dosage(req.getDosageInstructionFirstRep().getText())
                .frequency(req.getDosageInstructionFirstRep().getTiming().getRepeat().getFrequency() + " times per day")
                .prescriberName(req.getRequester().getDisplay())
                .status(Prescription.PrescriptionStatus.PENDING)
                .build();
        prescriptionRepository.save(prescription);
    }

    private void processObservation(org.hl7.fhir.r4.model.Observation obs, Resident resident) {
        com.clbc.model.Observation observation = com.clbc.model.Observation.builder()
                .residentId(resident.getId())
                .communityId(resident.getCommunityId())
                .type(obs.getCode().getText())
                .value(obs.getValueQuantity().getValue().toString())
                .unit(obs.getValueQuantity().getUnit())
                .observedAt(obs.getEffectiveDateTimeType().getValue().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .build();
        observationRepository.save(observation);
    }

    private void processAllergy(AllergyIntolerance ai, Resident resident) {
        Allergy allergy = Allergy.builder()
                .residentId(resident.getId())
                .communityId(resident.getCommunityId())
                .allergen(ai.getCode().getText())
                .severity(ai.getCriticality() != null ? ai.getCriticality().toCode() : "unknown")
                .reaction(ai.getReactionFirstRep().getManifestationFirstRep().getText())
                .build();
        allergyRepository.save(allergy);
    }

    private void processCondition(Condition c, Resident resident) {
        MedicalCondition condition = MedicalCondition.builder()
                .residentId(resident.getId())
                .communityId(resident.getCommunityId())
                .conditionName(c.getCode().getText())
                .status(c.getClinicalStatus().getCodingFirstRep().getCode())
                .onsetDate(c.hasOnsetDateTimeType()
                        ? c.getOnsetDateTimeType().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        : null)
                .build();
        medicalConditionRepository.save(condition);
    }
}
