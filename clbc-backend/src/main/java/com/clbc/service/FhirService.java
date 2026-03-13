package com.clbc.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.clbc.model.FhirResource;
import com.clbc.model.Resident;
import com.clbc.repository.FhirRepository;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FhirService {

    private final FhirRepository fhirRepository;
    private final FhirContext fhirContext = FhirContext.forR4();

    @Transactional
    public void exportResidentToFhir(Resident resident) {
        Patient patient = new Patient();
        patient.setId(resident.getId());
        patient.addName()
                .setFamily(resident.getLastName())
                .addGiven(resident.getFirstName());

        if (resident.getDateOfBirth() != null) {
            patient.setBirthDate(Date.from(resident.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        if (resident.getGender() != null) {
            try {
                patient.setGender(Enumerations.AdministrativeGender.fromCode(resident.getGender().toLowerCase()));
            } catch (Exception e) {
                // Ignore invalid gender codes for now
            }
        }

        IParser parser = fhirContext.newJsonParser();
        String json = parser.encodeResourceToString(patient);

        FhirResource resource = FhirResource.builder()
                .residentId(resident.getId())
                .communityId(resident.getCommunityId())
                .resourceType("Patient")
                .fhirId(resident.getId())
                .fhirJson(json)
                .sourceProvider("CLBC_INTERNAL")
                .build();

        fhirRepository.save(resource);
    }
}
