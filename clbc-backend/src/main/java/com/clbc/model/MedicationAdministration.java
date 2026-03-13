package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_administrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationAdministration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_id", nullable = false, length = 50)
    private String prescriptionId;

    @Column(name = "community_id", nullable = false, length = 50)
    private String communityId;

    @Column(name = "date_administered", nullable = false)
    private LocalDate dateAdministered;

    @Column(name = "time_administered", nullable = false)
    private LocalTime timeAdministered;

    @Column(name = "administered_by")
    private String administeredBy;

    @Column(name = "dose_taken")
    private Boolean doseTaken;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateAdministered == null) {
            dateAdministered = LocalDate.now();
        }
        if (timeAdministered == null) {
            timeAdministered = LocalTime.now();
        }
    }
}
