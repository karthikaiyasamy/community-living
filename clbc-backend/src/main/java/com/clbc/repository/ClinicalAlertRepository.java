package com.clbc.repository;

import com.clbc.model.ClinicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClinicalAlertRepository extends JpaRepository<ClinicalAlert, Long> {
    List<ClinicalAlert> findByCommunityIdAndIsResolvedFalseOrderByCreatedAtDesc(String communityId);

    List<ClinicalAlert> findByResidentIdAndCommunityIdOrderByCreatedAtDesc(String residentId, String communityId);
}
