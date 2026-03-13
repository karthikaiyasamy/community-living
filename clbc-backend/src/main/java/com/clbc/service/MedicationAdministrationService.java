package com.clbc.service;

import com.clbc.dto.MedicationAdministrationDTO;
import com.clbc.model.MedicationAdministration;
import com.clbc.repository.MedicationAdministrationRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationAdministrationService {

    private final MedicationAdministrationRepository medicationAdministrationRepository;
    private final AuditService auditService;

    public List<MedicationAdministrationDTO> getAdministrations(String prescriptionId) {
        String communityId = TenantContext.getTenantId();
        return medicationAdministrationRepository
                .findByPrescriptionIdAndCommunityIdOrderByDateAdministeredDescTimeAdministeredDesc(prescriptionId,
                        communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicationAdministrationDTO> getCommunityAdministrations(LocalDate date) {
        String communityId = TenantContext.getTenantId();
        return medicationAdministrationRepository.findByCommunityIdAndDateAdministered(communityId, date)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicationAdministrationDTO recordAdministration(String prescriptionId, MedicationAdministrationDTO dto) {
        String communityId = TenantContext.getTenantId();

        MedicationAdministration administration = MedicationAdministration.builder()
                .prescriptionId(prescriptionId)
                .communityId(communityId)
                .dateAdministered(dto.getDateAdministered())
                .timeAdministered(dto.getTimeAdministered())
                .administeredBy(dto.getAdministeredBy())
                .doseTaken(dto.getDoseTaken())
                .notes(dto.getNotes())
                .build();

        MedicationAdministration saved = medicationAdministrationRepository.save(administration);

        auditService.logAction("RECORD_MEDICATION_ADMIN", "PRESCRIPTION", prescriptionId,
                "Recorded medication dose: " + (saved.getDoseTaken() ? "Taken" : "Missed"), null);

        return mapToDTO(saved);
    }

    private MedicationAdministrationDTO mapToDTO(MedicationAdministration entity) {
        return MedicationAdministrationDTO.builder()
                .id(entity.getId())
                .prescriptionId(entity.getPrescriptionId())
                .dateAdministered(entity.getDateAdministered())
                .timeAdministered(entity.getTimeAdministered())
                .administeredBy(entity.getAdministeredBy())
                .doseTaken(entity.getDoseTaken())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
