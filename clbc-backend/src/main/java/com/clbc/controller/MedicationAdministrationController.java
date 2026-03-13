package com.clbc.controller;

import com.clbc.dto.MedicationAdministrationDTO;
import com.clbc.service.MedicationAdministrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions/{prescriptionId}/administrations")
@RequiredArgsConstructor
public class MedicationAdministrationController {

    private final MedicationAdministrationService medicationAdministrationService;

    @GetMapping
    public ResponseEntity<List<MedicationAdministrationDTO>> getAdministrations(@PathVariable String prescriptionId) {
        return ResponseEntity.ok(medicationAdministrationService.getAdministrations(prescriptionId));
    }

    @PostMapping
    public ResponseEntity<MedicationAdministrationDTO> recordAdministration(
            @PathVariable String prescriptionId,
            @RequestBody MedicationAdministrationDTO dto) {
        return ResponseEntity.ok(medicationAdministrationService.recordAdministration(prescriptionId, dto));
    }
}
