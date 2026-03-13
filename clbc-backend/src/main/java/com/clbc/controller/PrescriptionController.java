package com.clbc.controller;

import com.clbc.dto.PrescriptionDTO;
import com.clbc.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/residents/{residentId}/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptions(@PathVariable String residentId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByResident(residentId));
    }

    @PostMapping
    public ResponseEntity<PrescriptionDTO> addPrescription(
            @PathVariable String residentId,
            @RequestBody PrescriptionDTO dto) {
        return ResponseEntity.ok(prescriptionService.addPrescription(residentId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> discontinuePrescription(@PathVariable String id) {
        prescriptionService.discontinuePrescription(id);
        return ResponseEntity.noContent().build();
    }
}
