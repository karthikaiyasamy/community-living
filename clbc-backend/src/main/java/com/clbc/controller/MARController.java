package com.clbc.controller;

import com.clbc.dto.MedicationAdministrationDTO;
import com.clbc.service.MedicationAdministrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mar")
@RequiredArgsConstructor
public class MARController {

    private final MedicationAdministrationService medicationAdministrationService;

    @GetMapping("/daily")
    public ResponseEntity<List<MedicationAdministrationDTO>> getDailyLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(medicationAdministrationService.getCommunityAdministrations(targetDate));
    }
}
