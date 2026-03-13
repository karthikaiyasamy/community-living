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
public class ClinicalAlertDTO {
    private Long id;
    private String residentId;
    private String residentName;
    private String type;
    private String severity;
    private String message;
    private Boolean isResolved;
    private LocalDateTime createdAt;
}
