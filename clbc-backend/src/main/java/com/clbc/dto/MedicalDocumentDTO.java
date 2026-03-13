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
public class MedicalDocumentDTO {
    private String id;
    private String residentId;
    private String documentName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
