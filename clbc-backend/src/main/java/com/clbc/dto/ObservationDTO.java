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
public class ObservationDTO {
    private String id;
    private String residentId;
    private String type;
    private String value;
    private String unit;
    private LocalDateTime observedAt;
}
