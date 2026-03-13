package com.clbc.service;

import com.clbc.dto.ObservationDTO;
import com.clbc.model.Observation;
import com.clbc.repository.ObservationRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservationService {

    private final ObservationRepository observationRepository;

    public List<ObservationDTO> getObservations(String residentId) {
        String communityId = TenantContext.getTenantId();
        return observationRepository.findByResidentIdAndCommunityIdOrderByObservedAtDesc(residentId, communityId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ObservationDTO> getObservationsByType(String residentId, String type) {
        String communityId = TenantContext.getTenantId();
        return observationRepository
                .findByResidentIdAndCommunityIdAndTypeOrderByObservedAtAsc(residentId, communityId, type)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ObservationDTO addObservation(String residentId, ObservationDTO dto) {
        String communityId = TenantContext.getTenantId();
        Observation observation = Observation.builder()
                .residentId(residentId)
                .communityId(communityId)
                .type(dto.getType())
                .value(dto.getValue())
                .unit(dto.getUnit())
                .observedAt(dto.getObservedAt())
                .build();

        Observation saved = observationRepository.save(observation);
        return mapToDTO(saved);
    }

    private ObservationDTO mapToDTO(Observation entity) {
        return ObservationDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .type(entity.getType())
                .value(entity.getValue())
                .unit(entity.getUnit())
                .observedAt(entity.getObservedAt())
                .build();
    }
}
