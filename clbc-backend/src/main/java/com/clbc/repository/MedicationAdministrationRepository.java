package com.clbc.repository;

import com.clbc.model.MedicationAdministration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicationAdministrationRepository extends JpaRepository<MedicationAdministration, Long> {
    List<MedicationAdministration> findByPrescriptionIdAndCommunityIdOrderByDateAdministeredDescTimeAdministeredDesc(
            String prescriptionId, String communityId);

    List<MedicationAdministration> findByPrescriptionIdAndDateAdministered(String prescriptionId,
            LocalDate dateAdministered);

    List<MedicationAdministration> findByCommunityIdAndDateAdministered(String communityId, LocalDate dateAdministered);
}
