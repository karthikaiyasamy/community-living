package com.clbc.repository;

import com.clbc.model.ResidentPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<ResidentPreference, Long> {
    Optional<ResidentPreference> findByResidentIdAndCommunityId(String residentId, String communityId);
}
