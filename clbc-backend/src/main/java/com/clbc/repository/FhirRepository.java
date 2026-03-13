package com.clbc.repository;

import com.clbc.model.FhirResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FhirRepository extends JpaRepository<FhirResource, Long> {
    List<FhirResource> findByResidentIdAndCommunityId(String residentId, String communityId);
}
