package com.clbc.repository;

import com.clbc.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, String> {
    List<Prescription> findByResidentIdAndCommunityId(String residentId, String communityId);

    List<Prescription> findByResidentIdAndCommunityIdAndStatus(String residentId, String communityId,
            Prescription.PrescriptionStatus status);

    List<Prescription> findByCommunityIdAndStatus(String communityId, Prescription.PrescriptionStatus status);
}
