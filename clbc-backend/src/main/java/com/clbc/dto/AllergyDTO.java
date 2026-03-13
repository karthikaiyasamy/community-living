package com.clbc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllergyDTO {
    private String id;
    private String residentId;
    private String allergen;
    private String severity;
    private String reaction;
}
