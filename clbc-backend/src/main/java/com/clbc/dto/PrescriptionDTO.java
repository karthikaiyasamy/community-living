package com.clbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {
    private String id;
    private String residentId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String route;
    private String prescriberName;
    private LocalDate datePrescribed;
    private String indication;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
