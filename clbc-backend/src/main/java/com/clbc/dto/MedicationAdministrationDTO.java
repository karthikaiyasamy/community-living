package com.clbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationAdministrationDTO {
    private Long id;
    private String prescriptionId;
    private LocalDate dateAdministered;
    private LocalTime timeAdministered;
    private String administeredBy;
    private Boolean doseTaken;
    private String notes;
    private LocalDateTime createdAt;
}
