package com.clbc.controller;

import com.clbc.dto.MedicalConditionDTO;
import com.clbc.service.MedicalConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/residents/{residentId}/conditions")
@RequiredArgsConstructor
public class MedicalConditionController {

    private final MedicalConditionService service;

    @GetMapping
    public ResponseEntity<List<MedicalConditionDTO>> getConditions(@PathVariable String residentId) {
        return ResponseEntity.ok(service.getConditions(residentId));
    }

    @PostMapping
    public ResponseEntity<MedicalConditionDTO> addCondition(
            @PathVariable String residentId,
            @RequestBody MedicalConditionDTO dto) {
        return ResponseEntity.ok(service.addCondition(residentId, dto));
    }
}
