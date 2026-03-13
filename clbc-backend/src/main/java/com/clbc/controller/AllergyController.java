package com.clbc.controller;

import com.clbc.dto.AllergyDTO;
import com.clbc.service.AllergyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/residents/{residentId}/allergies")
@RequiredArgsConstructor
public class AllergyController {

    private final AllergyService service;

    @GetMapping
    public ResponseEntity<List<AllergyDTO>> getAllergies(@PathVariable String residentId) {
        return ResponseEntity.ok(service.getAllergies(residentId));
    }

    @PostMapping
    public ResponseEntity<AllergyDTO> addAllergy(
            @PathVariable String residentId,
            @RequestBody AllergyDTO dto) {
        return ResponseEntity.ok(service.addAllergy(residentId, dto));
    }
}
