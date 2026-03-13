package com.clbc.repository;

import com.clbc.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, String> {
    List<Allergy> findByResidentIdAndCommunityId(String residentId, String communityId);
}
