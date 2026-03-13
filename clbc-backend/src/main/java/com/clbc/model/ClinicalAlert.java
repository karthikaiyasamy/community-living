package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicalAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    @Column(name = "resident_id", nullable = false)
    private String residentId;

    @Column(name = "type", nullable = false)
    private String type; // MISSING_DOSE, VITAL_ABNORMALITY

    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "source_id")
    private String sourceId; // Reference to Prescription, Observation, etc.

    @Column(name = "is_resolved")
    private Boolean isResolved;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private String resolvedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum AlertSeverity {
        INFO, WARNING, CRITICAL
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isResolved == null) {
            isResolved = false;
        }
    }
}
