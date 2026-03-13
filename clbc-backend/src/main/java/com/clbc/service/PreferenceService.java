package com.clbc.service;

import com.clbc.dto.PreferenceDTO;
import com.clbc.model.ResidentPreference;
import com.clbc.repository.PreferenceRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepository preferenceRepository;

    public PreferenceDTO getPreferences(String residentId) {
        String communityId = TenantContext.getTenantId();
        ResidentPreference preference = preferenceRepository.findByResidentIdAndCommunityId(residentId, communityId)
                .orElse(ResidentPreference.builder()
                        .residentId(residentId)
                        .communityId(communityId)
                        .build());
        return mapToDTO(preference);
    }

    @Transactional
    public PreferenceDTO updatePreferences(String residentId, PreferenceDTO dto) {
        String communityId = TenantContext.getTenantId();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        ResidentPreference preference = preferenceRepository.findByResidentIdAndCommunityId(residentId, communityId)
                .orElse(ResidentPreference.builder()
                        .residentId(residentId)
                        .communityId(communityId)
                        .build());

        preference.setDietaryPreferences(dto.getDietaryPreferences());
        preference.setActivityPreferences(dto.getActivityPreferences());
        preference.setCommunicationPreferences(dto.getCommunicationPreferences());
        preference.setCareNotes(dto.getCareNotes());
        preference.setLastUpdatedBy(currentUser);

        ResidentPreference saved = preferenceRepository.save(preference);
        return mapToDTO(saved);
    }

    private PreferenceDTO mapToDTO(ResidentPreference entity) {
        return PreferenceDTO.builder()
                .id(entity.getId())
                .residentId(entity.getResidentId())
                .dietaryPreferences(entity.getDietaryPreferences())
                .activityPreferences(entity.getActivityPreferences())
                .communicationPreferences(entity.getCommunicationPreferences())
                .careNotes(entity.getCareNotes())
                .lastUpdatedBy(entity.getLastUpdatedBy())
                .lastUpdatedAt(entity.getLastUpdatedAt())
                .build();
    }
}
