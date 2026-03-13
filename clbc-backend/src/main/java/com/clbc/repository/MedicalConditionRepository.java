package com.clbc.repository;

import com.clbc.model.MedicalCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalConditionRepository extends JpaRepository<MedicalCondition, String> {
    List<MedicalCondition> findByResidentIdAndCommunityId(String residentId, String communityId);
}
