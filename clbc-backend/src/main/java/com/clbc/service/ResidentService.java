package com.clbc.service;

import com.clbc.dto.ResidentDTO;
import com.clbc.model.Resident;
import com.clbc.repository.ResidentRepository;
import com.clbc.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResidentService {

    private final ResidentRepository residentRepository;
    private final AuditService auditService;

    public Page<ResidentDTO> listResidents(Pageable pageable, String status) {
        String communityId = TenantContext.getTenantId();
        Page<Resident> residents;
        if (status != null) {
            residents = residentRepository.findByCommunityIdAndStatus(
                    communityId, Resident.ResidentStatus.valueOf(status.toUpperCase()), pageable);
        } else {
            residents = residentRepository.findByCommunityId(communityId, pageable);
        }
        return residents.map(this::convertToDTO);
    }

    public ResidentDTO getResident(String id) {
        String communityId = TenantContext.getTenantId();
        ResidentDTO resident = residentRepository.findByIdAndCommunityId(id, communityId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        auditService.logAction("VIEW_RESIDENT", "RESIDENT", id, "Viewed resident details", null);
        return resident;
    }

    @Transactional
    public ResidentDTO createResident(ResidentDTO dto) {
        String communityId = TenantContext.getTenantId();
        Resident resident = Resident.builder()
                .id(UUID.randomUUID().toString())
                .communityId(communityId)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .primaryPhysician(dto.getPrimaryPhysician())
                .physicianContact(dto.getPhysicianContact())
                .status(Resident.ResidentStatus.ACTIVE)
                .build();

        Resident saved = residentRepository.save(resident);
        auditService.logAction("CREATE_RESIDENT", "RESIDENT", saved.getId(),
                "Created new resident: " + saved.getFirstName() + " " + saved.getLastName(), null);
        return convertToDTO(saved);
    }

    @Transactional
    public ResidentDTO updateResident(String id, ResidentDTO dto) {
        String communityId = TenantContext.getTenantId();
        Resident resident = residentRepository.findByIdAndCommunityId(id, communityId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        resident.setFirstName(dto.getFirstName());
        resident.setLastName(dto.getLastName());
        resident.setDateOfBirth(dto.getDateOfBirth());
        resident.setGender(dto.getGender());
        resident.setEmail(dto.getEmail());
        resident.setPhone(dto.getPhone());
        resident.setPrimaryPhysician(dto.getPrimaryPhysician());
        resident.setPhysicianContact(dto.getPhysicianContact());
        if (dto.getStatus() != null) {
            resident.setStatus(Resident.ResidentStatus.valueOf(dto.getStatus().toUpperCase()));
        }

        Resident saved = residentRepository.save(resident);
        auditService.logAction("UPDATE_RESIDENT", "RESIDENT", id, "Updated resident information", null);
        return convertToDTO(saved);
    }

    @Transactional
    public void dischargeResident(String id) {
        String communityId = TenantContext.getTenantId();
        Resident resident = residentRepository.findByIdAndCommunityId(id, communityId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));
        resident.setStatus(Resident.ResidentStatus.DISCHARGED);
        residentRepository.save(resident);
        auditService.logAction("DISCHARGE_RESIDENT", "RESIDENT", id, "Discharged resident", null);
    }

    @Transactional
    public void activateResident(String id) {
        String communityId = TenantContext.getTenantId();
        Resident resident = residentRepository.findByIdAndCommunityId(id, communityId)
                .orElseThrow(() -> new RuntimeException("Resident not found"));
        resident.setStatus(Resident.ResidentStatus.ACTIVE);
        residentRepository.save(resident);
        auditService.logAction("ACTIVATE_RESIDENT", "RESIDENT", id, "Activated resident after clinical review", null);
    }

    private ResidentDTO convertToDTO(Resident resident) {
        return ResidentDTO.builder()
                .id(resident.getId())
                .firstName(resident.getFirstName())
                .lastName(resident.getLastName())
                .dateOfBirth(resident.getDateOfBirth())
                .gender(resident.getGender())
                .email(resident.getEmail())
                .phone(resident.getPhone())
                .primaryPhysician(resident.getPrimaryPhysician())
                .physicianContact(resident.getPhysicianContact())
                .status(resident.getStatus().name())
                .build();
    }
}
