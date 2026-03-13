package com.clbc.service;

import com.clbc.dto.ClinicalAlertDTO;
import com.clbc.model.ClinicalAlert;
import com.clbc.model.MedicationAdministration;
import com.clbc.model.Prescription;
import com.clbc.model.Resident;
import com.clbc.repository.ClinicalAlertRepository;
import com.clbc.repository.MedicationAdministrationRepository;
import com.clbc.repository.PrescriptionRepository;
import com.clbc.repository.ResidentRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final ClinicalAlertRepository alertRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicationAdministrationRepository administrationRepository;
    private final ResidentRepository residentRepository;

    public List<ClinicalAlertDTO> getActiveAlerts() {
        String communityId = TenantContext.getTenantId();
        // Automatically check for missing doses when fetching alerts for demo purposes
        checkMissingDoses(communityId);

        return alertRepository.findByCommunityIdAndIsResolvedFalseOrderByCreatedAtDesc(communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkMissingDoses(String communityId) {
        List<Prescription> activePrescriptions = prescriptionRepository.findByCommunityIdAndStatus(
                communityId, Prescription.PrescriptionStatus.ACTIVE);

        LocalDate today = LocalDate.now();

        for (Prescription presc : activePrescriptions) {
            List<MedicationAdministration> todayAdmins = administrationRepository
                    .findByPrescriptionIdAndDateAdministered(presc.getId(), today);

            boolean takenToday = todayAdmins.stream().anyMatch(MedicationAdministration::getDoseTaken);

            if (!takenToday && LocalDateTime.now().getHour() >= 9) { // Simple check: alert if not taken by 9 AM
                createMissingDoseAlert(presc, communityId);
            }
        }
    }

    private void createMissingDoseAlert(Prescription presc, String communityId) {
        String alertType = "MISSING_DOSE";
        String message = String.format("Missing Dose: %s (%s) scheduled for today has not been administered.",
                presc.getMedicationName(), presc.getDosage());

        // Avoid duplicate alerts for same prescription on same day
        List<ClinicalAlert> existing = alertRepository.findByResidentIdAndCommunityIdOrderByCreatedAtDesc(
                presc.getResidentId(), communityId);

        boolean alreadyAlerted = existing.stream()
                .anyMatch(a -> a.getType().equals(alertType) &&
                        a.getSourceId().equals(presc.getId()) &&
                        a.getCreatedAt().toLocalDate().equals(LocalDate.now()));

        if (!alreadyAlerted) {
            ClinicalAlert alert = ClinicalAlert.builder()
                    .communityId(communityId)
                    .residentId(presc.getResidentId())
                    .type(alertType)
                    .severity(ClinicalAlert.AlertSeverity.WARNING)
                    .message(message)
                    .sourceId(presc.getId())
                    .isResolved(false)
                    .build();
            alertRepository.save(alert);
        }
    }

    @Transactional
    public void resolveAlert(Long alertId, String username) {
        ClinicalAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setIsResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(username);
        alertRepository.save(alert);
    }

    private ClinicalAlertDTO mapToDTO(ClinicalAlert entity) {
        Resident resident = residentRepository.findById(entity.getResidentId()).orElse(null);
        String residentName = resident != null ? resident.getFirstName() + " " + resident.getLastName() : "Unknown";

        return ClinicalAlertDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .residentName(residentName)
                .type(entity.getType())
                .severity(entity.getSeverity().name())
                .message(entity.getMessage())
                .isResolved(entity.getIsResolved())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
