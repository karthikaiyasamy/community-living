package com.clbc.controller;

import com.clbc.dto.ObservationDTO;
import com.clbc.service.ObservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/residents/{residentId}/observations")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;

    @GetMapping
    public ResponseEntity<List<ObservationDTO>> getObservations(
            @PathVariable String residentId,
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(observationService.getObservationsByType(residentId, type));
        }
        return ResponseEntity.ok(observationService.getObservations(residentId));
    }

    @PostMapping
    public ResponseEntity<ObservationDTO> addObservation(
            @PathVariable String residentId,
            @RequestBody ObservationDTO dto) {
        return ResponseEntity.ok(observationService.addObservation(residentId, dto));
    }
}
