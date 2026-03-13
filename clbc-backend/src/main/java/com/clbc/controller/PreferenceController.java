package com.clbc.controller;

import com.clbc.dto.PreferenceDTO;
import com.clbc.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents/{residentId}/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping
    public ResponseEntity<PreferenceDTO> getPreferences(@PathVariable String residentId) {
        return ResponseEntity.ok(preferenceService.getPreferences(residentId));
    }

    @PutMapping
    public ResponseEntity<PreferenceDTO> updatePreferences(
            @PathVariable String residentId,
            @RequestBody PreferenceDTO preferenceDTO) {
        return ResponseEntity.ok(preferenceService.updatePreferences(residentId, preferenceDTO));
    }
}
