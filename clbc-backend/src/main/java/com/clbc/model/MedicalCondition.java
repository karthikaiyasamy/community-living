package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conditions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalCondition {

    @Id
    @Column(length = 50)
    private String id;

    @Column(name = "resident_id", nullable = false, length = 50)
    private String residentId;

    @Column(name = "community_id", nullable = false, length = 50)
    private String communityId;

    @Column(name = "condition_name", nullable = false)
    private String conditionName;

    @Column(name = "on_set_date")
    private LocalDate onsetDate;

    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
}
