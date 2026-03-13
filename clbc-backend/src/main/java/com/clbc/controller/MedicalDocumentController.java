package com.clbc.controller;

import com.clbc.dto.MedicalDocumentDTO;
import com.clbc.model.MedicalDocument;
import com.clbc.service.MedicalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/residents/{residentId}/documents")
@RequiredArgsConstructor
public class MedicalDocumentController {

    private final MedicalDocumentService medicalDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<MedicalDocumentDTO> uploadDocument(
            @PathVariable String residentId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(medicalDocumentService.uploadDocument(residentId, file));
    }

    @GetMapping
    public ResponseEntity<List<MedicalDocumentDTO>> getDocuments(@PathVariable String residentId) {
        return ResponseEntity.ok(medicalDocumentService.getDocuments(residentId));
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable String residentId,
            @PathVariable String documentId) throws IOException {

        MedicalDocument info = medicalDocumentService.getDocumentInfo(documentId);
        byte[] content = medicalDocumentService.getDocumentContent(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(info.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + info.getDocumentName() + "\"")
                .body(new ByteArrayResource(content));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String residentId,
            @PathVariable String documentId) throws IOException {
        medicalDocumentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }
}
