package com.clbc.repository;

import com.clbc.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, String> {
    List<MedicalDocument> findByResidentIdAndCommunityIdOrderByUploadedAtDesc(String residentId, String communityId);
}
