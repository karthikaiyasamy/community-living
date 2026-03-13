package com.clbc.controller;

import com.clbc.service.FhirIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/fhir")
@RequiredArgsConstructor
public class FhirIngestionController {

    private final FhirIngestionService fhirIngestionService;

    @PostMapping("/ingest")
    @PreAuthorize("hasAuthority('SYSTEM_INTEGRATION')")
    public ResponseEntity<Map<String, Object>> ingestFhirBundle(@RequestBody String body) {
        fhirIngestionService.ingestBundle(body);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "FHIR bundle received and queued for processing"));
    }
}
