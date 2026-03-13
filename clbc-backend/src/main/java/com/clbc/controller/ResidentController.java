package com.clbc.controller;

import com.clbc.dto.ResidentDTO;
import com.clbc.service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @GetMapping
    public ResponseEntity<Page<ResidentDTO>> listResidents(
            Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(residentService.listResidents(pageable, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidentDTO> getResident(@PathVariable String id) {
        return ResponseEntity.ok(residentService.getResident(id));
    }

    @PostMapping
    public ResponseEntity<ResidentDTO> createResident(@RequestBody ResidentDTO dto) {
        return ResponseEntity.ok(residentService.createResident(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResidentDTO> updateResident(@PathVariable String id, @RequestBody ResidentDTO dto) {
        return ResponseEntity.ok(residentService.updateResident(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dischargeResident(@PathVariable String id) {
        residentService.dischargeResident(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateResident(@PathVariable String id) {
        residentService.activateResident(id);
        return ResponseEntity.ok().build();
    }
}
