package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    private String id;

    @Column(name = "resident_id", nullable = false)
    private String residentId;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    private String dosage;
    private String frequency;
    private String route;

    @Column(name = "prescriber_name")
    private String prescriberName;

    @Column(name = "prescriber_license")
    private String prescriberLicense;

    @Column(name = "date_prescribed")
    private LocalDate datePrescribed;

    @Column(name = "date_discontinued")
    private LocalDate dateDiscontinued;

    private String indication;
    private String sideEffects;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PrescriptionStatus {
        ACTIVE, DISCONTINUED, PAUSED, PENDING
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
