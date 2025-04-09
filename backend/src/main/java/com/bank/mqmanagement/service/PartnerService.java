package com.bank.mqmanagement.service;

import com.bank.mqmanagement.dto.PartnerDTO;
import com.bank.mqmanagement.exception.ResourceNotFoundException;
import com.bank.mqmanagement.model.Partner;
import com.bank.mqmanagement.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerDTO createPartner(PartnerDTO partnerDTO) {
        log.debug("Creating new partner with alias: {}", partnerDTO.getAlias());

        if (partnerRepository.existsByAlias(partnerDTO.getAlias())) {
            throw new DataIntegrityViolationException("Un partenaire avec cet alias existe déjà");
        }

        Partner partner = Partner.builder()
                .alias(partnerDTO.getAlias())
                .type(partnerDTO.getType())
                .direction(partnerDTO.getDirection())
                .application(partnerDTO.getApplication())
                .processedFlowType(partnerDTO.getProcessedFlowType())
                .description(partnerDTO.getDescription())
                .build();

        Partner savedPartner = partnerRepository.save(partner);
        return mapToDTO(savedPartner);
    }

    @Transactional(readOnly = true)
    public Page<PartnerDTO> getAllPartners(Pageable pageable) {
        log.debug("Fetching all partners with pagination");
        Page<Partner> partnerPage = partnerRepository.findAllByOrderByAliasAsc(pageable);
        return partnerPage.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public PartnerDTO getPartnerById(Long id) {
        log.debug("Fetching partner with ID: {}", id);
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + id));
        return mapToDTO(partner);
    }

    @Transactional(readOnly = true)
    public PartnerDTO getPartnerByAlias(String alias) {
        log.debug("Fetching partner with alias: {}", alias);
        Partner partner = partnerRepository.findByAlias(alias)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with alias: " + alias));
        return mapToDTO(partner);
    }

    @Transactional
    public PartnerDTO updatePartner(Long id, PartnerDTO partnerDTO) {
        log.debug("Updating partner with ID: {}", id);

        Partner existingPartner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + id));

        // Vérifier si l'alias mis à jour existe déjà pour un autre partenaire
        if (!existingPartner.getAlias().equals(partnerDTO.getAlias()) &&
                partnerRepository.existsByAlias(partnerDTO.getAlias())) {
            throw new DataIntegrityViolationException("Un partenaire avec cet alias existe déjà");
        }

        existingPartner.setAlias(partnerDTO.getAlias());
        existingPartner.setType(partnerDTO.getType());
        existingPartner.setDirection(partnerDTO.getDirection());
        existingPartner.setApplication(partnerDTO.getApplication());
        existingPartner.setProcessedFlowType(partnerDTO.getProcessedFlowType());
        existingPartner.setDescription(partnerDTO.getDescription());

        Partner updatedPartner = partnerRepository.save(existingPartner);
        return mapToDTO(updatedPartner);
    }

    @Transactional
    public void deletePartner(Long id) {
        log.debug("Deleting partner with ID: {}", id);

        if (!partnerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Partner not found with id: " + id);
        }

        partnerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<PartnerDTO> searchPartners(String keyword, Pageable pageable) {
        log.debug("Searching partners with keyword: {}", keyword);
        return partnerRepository.search(keyword, pageable)
                .map(this::mapToDTO);
    }

    private PartnerDTO mapToDTO(Partner partner) {
        return PartnerDTO.builder()
                .id(partner.getId())
                .alias(partner.getAlias())
                .type(partner.getType())
                .direction(partner.getDirection())
                .application(partner.getApplication())
                .processedFlowType(partner.getProcessedFlowType())
                .description(partner.getDescription())
                .createdAt(partner.getCreatedAt())
                .updatedAt(partner.getUpdatedAt())
                .build();
    }
}