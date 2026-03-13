package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "observations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Observation {

    @Id
    private String id;

    @Column(name = "resident_id", nullable = false)
    private String residentId;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    private String type;
    private String value;
    private String unit;

    @Column(name = "observed_at")
    private LocalDateTime observedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (observedAt == null) {
            observedAt = LocalDateTime.now();
        }
    }
}
