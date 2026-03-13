package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "resident_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_id", nullable = false, length = 50)
    private String residentId;

    @Column(name = "community_id", nullable = false, length = 50)
    private String communityId;

    @Column(name = "dietary_preferences")
    private String dietaryPreferences;

    @Column(name = "activity_preferences")
    private String activityPreferences;

    @Column(name = "communication_preferences")
    private String communicationPreferences;

    @Column(name = "care_notes")
    private String careNotes;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @PreUpdate
    @PrePersist
    public void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }
}
