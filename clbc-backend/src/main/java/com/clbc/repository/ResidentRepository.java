package com.clbc.repository;

import com.clbc.model.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, String> {
    Page<Resident> findByCommunityId(String communityId, Pageable pageable);

    Optional<Resident> findByIdAndCommunityId(String id, String communityId);

    Optional<Resident> findByMrnAndCommunityId(String mrn, String communityId);

    Page<Resident> findByCommunityIdAndStatus(String communityId, Resident.ResidentStatus status, Pageable pageable);
}
