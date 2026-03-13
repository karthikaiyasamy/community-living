package com.clbc.repository;

import com.clbc.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, String> {
    List<Observation> findByResidentIdAndCommunityIdOrderByObservedAtDesc(String residentId, String communityId);

    List<Observation> findByResidentIdAndCommunityIdAndTypeOrderByObservedAtAsc(String residentId, String communityId,
            String type);
}
