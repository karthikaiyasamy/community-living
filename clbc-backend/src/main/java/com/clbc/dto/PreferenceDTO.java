package com.clbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceDTO {
    private Long id;
    private String residentId;
    private String dietaryPreferences;
    private String activityPreferences;
    private String communicationPreferences;
    private String careNotes;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
