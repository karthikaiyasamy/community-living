package com.clbc.service;

import com.clbc.dto.MedicalConditionDTO;
import com.clbc.model.MedicalCondition;
import com.clbc.repository.MedicalConditionRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalConditionService {

    private final MedicalConditionRepository repository;

    public List<MedicalConditionDTO> getConditions(String residentId) {
        String communityId = TenantContext.getTenantId();
        return repository.findByResidentIdAndCommunityId(residentId, communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicalConditionDTO addCondition(String residentId, MedicalConditionDTO dto) {
        String communityId = TenantContext.getTenantId();
        MedicalCondition condition = MedicalCondition.builder()
                .residentId(residentId)
                .communityId(communityId)
                .conditionName(dto.getConditionName())
                .onsetDate(dto.getOnsetDate())
                .status(dto.getStatus())
                .build();

        MedicalCondition saved = repository.save(condition);
        return mapToDTO(saved);
    }

    private MedicalConditionDTO mapToDTO(MedicalCondition entity) {
        return MedicalConditionDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .conditionName(entity.getConditionName())
                .onsetDate(entity.getOnsetDate())
                .status(entity.getStatus())
                .build();
    }
}
