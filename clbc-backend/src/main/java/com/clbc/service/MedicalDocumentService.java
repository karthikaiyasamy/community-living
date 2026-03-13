package com.clbc.service;

import com.clbc.dto.MedicalDocumentDTO;
import com.clbc.model.MedicalDocument;
import com.clbc.repository.MedicalDocumentRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalDocumentService {

    private final MedicalDocumentRepository medicalDocumentRepository;
    private final AuditService auditService;

    @Value("${clbc.upload.dir}")
    private String uploadDir;

    @Transactional
    public MedicalDocumentDTO uploadDocument(String residentId, MultipartFile file) throws IOException {
        String communityId = TenantContext.getTenantId();

        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir, communityId, residentId);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileId = UUID.randomUUID().toString();
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String storedFileName = fileId + extension;
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath);

        MedicalDocument document = MedicalDocument.builder()
                .id(fileId)
                .residentId(residentId)
                .communityId(communityId)
                .documentName(originalFileName)
                .filePath(filePath.toString())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        MedicalDocument saved = medicalDocumentRepository.save(document);
        auditService.logAction("UPLOAD_DOCUMENT", "MEDICAL_DOCUMENT", saved.getId(),
                "Uploaded document: " + originalFileName + " for resident: " + residentId, null);

        return mapToDTO(saved);
    }

    public List<MedicalDocumentDTO> getDocuments(String residentId) {
        String communityId = TenantContext.getTenantId();
        return medicalDocumentRepository.findByResidentIdAndCommunityIdOrderByUploadedAtDesc(residentId, communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public byte[] getDocumentContent(String documentId) throws IOException {
        String communityId = TenantContext.getTenantId();
        MedicalDocument document = medicalDocumentRepository.findById(documentId)
                .filter(d -> d.getCommunityId().equals(communityId))
                .orElseThrow(() -> new RuntimeException("Document not found"));

        return Files.readAllBytes(Paths.get(document.getFilePath()));
    }

    public MedicalDocument getDocumentInfo(String documentId) {
        String communityId = TenantContext.getTenantId();
        return medicalDocumentRepository.findById(documentId)
                .filter(d -> d.getCommunityId().equals(communityId))
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    @Transactional
    public void deleteDocument(String documentId) throws IOException {
        String communityId = TenantContext.getTenantId();
        MedicalDocument document = medicalDocumentRepository.findById(documentId)
                .filter(d -> d.getCommunityId().equals(communityId))
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Files.deleteIfExists(Paths.get(document.getFilePath()));
        medicalDocumentRepository.delete(document);

        auditService.logAction("DELETE_DOCUMENT", "MEDICAL_DOCUMENT", documentId,
                "Deleted document: " + document.getDocumentName(), null);
    }

    private MedicalDocumentDTO mapToDTO(MedicalDocument entity) {
        return MedicalDocumentDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .documentName(entity.getDocumentName())
                .contentType(entity.getContentType())
                .fileSize(entity.getFileSize())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }
}
