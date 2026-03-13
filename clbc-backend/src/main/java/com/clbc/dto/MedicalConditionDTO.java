package com.clbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalConditionDTO {
    private String id;
    private String residentId;
    private String conditionName;
    private LocalDate onsetDate;
    private String status;
}
