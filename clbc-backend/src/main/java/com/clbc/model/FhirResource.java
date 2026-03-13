package com.clbc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "fhir_resources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FhirResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_id")
    private String residentId;

    @Column(name = "community_id", nullable = false)
    private String communityId;

    @Column(name = "resource_type", nullable = false)
    private String resourceType;

    @Column(name = "fhir_id")
    private String fhirId;

    @Column(name = "fhir_json", nullable = false, columnDefinition = "JSON")
    private String fhirJson;

    @Column(name = "source_provider")
    private String sourceProvider;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        receivedAt = LocalDateTime.now();
    }
}
