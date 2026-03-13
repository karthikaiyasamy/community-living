package com.clbc.service;

import com.clbc.dto.AllergyDTO;
import com.clbc.model.Allergy;
import com.clbc.repository.AllergyRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllergyService {

    private final AllergyRepository repository;

    public List<AllergyDTO> getAllergies(String residentId) {
        String communityId = TenantContext.getTenantId();
        return repository.findByResidentIdAndCommunityId(residentId, communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AllergyDTO addAllergy(String residentId, AllergyDTO dto) {
        String communityId = TenantContext.getTenantId();
        Allergy allergy = Allergy.builder()
                .residentId(residentId)
                .communityId(communityId)
                .allergen(dto.getAllergen())
                .severity(dto.getSeverity())
                .reaction(dto.getReaction())
                .build();

        Allergy saved = repository.save(allergy);
        return mapToDTO(saved);
    }

    private AllergyDTO mapToDTO(Allergy entity) {
        return AllergyDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .allergen(entity.getAllergen())
                .severity(entity.getSeverity())
                .reaction(entity.getReaction())
                .build();
    }
}
