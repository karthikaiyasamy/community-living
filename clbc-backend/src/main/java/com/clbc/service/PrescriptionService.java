package com.clbc.service;

import com.clbc.dto.PrescriptionDTO;
import com.clbc.model.Prescription;
import com.clbc.repository.PrescriptionRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public List<PrescriptionDTO> getPrescriptionsByResident(String residentId) {
        String communityId = TenantContext.getTenantId();
        return prescriptionRepository.findByResidentIdAndCommunityId(residentId, communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrescriptionDTO addPrescription(String residentId, PrescriptionDTO dto) {
        String communityId = TenantContext.getTenantId();
        Prescription prescription = Prescription.builder()
                .residentId(residentId)
                .communityId(communityId)
                .medicationName(dto.getMedicationName())
                .dosage(dto.getDosage())
                .frequency(dto.getFrequency())
                .route(dto.getRoute())
                .prescriberName(dto.getPrescriberName())
                .datePrescribed(dto.getDatePrescribed())
                .indication(dto.getIndication())
                .status(Prescription.PrescriptionStatus.valueOf(dto.getStatus() != null ? dto.getStatus() : "ACTIVE"))
                .build();

        Prescription saved = prescriptionRepository.save(prescription);
        return mapToDTO(saved);
    }

    @Transactional
    public void discontinuePrescription(String id) {
        String communityId = TenantContext.getTenantId();
        Prescription prescription = prescriptionRepository.findById(id)
                .filter(p -> p.getCommunityId().equals(communityId))
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setStatus(Prescription.PrescriptionStatus.DISCONTINUED);
        prescriptionRepository.save(prescription);
    }

    private PrescriptionDTO mapToDTO(Prescription entity) {
        return PrescriptionDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .medicationName(entity.getMedicationName())
                .dosage(entity.getDosage())
                .frequency(entity.getFrequency())
                .route(entity.getRoute())
                .prescriberName(entity.getPrescriberName())
                .datePrescribed(entity.getDatePrescribed())
                .indication(entity.getIndication())
                .status(entity.getStatus().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
