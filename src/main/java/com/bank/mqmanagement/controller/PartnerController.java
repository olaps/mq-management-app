package com.bank.mqmanagement.controller;

import com.bank.mqmanagement.dto.PartnerDTO;
import com.bank.mqmanagement.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Partner API", description = "API pour la gestion des partenaires MQ")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping
    @Operation(summary = "Créer un nouveau partenaire", description = "Crée un nouveau partenaire MQ")
    public ResponseEntity<PartnerDTO> createPartner(
            @Valid @RequestBody PartnerDTO partnerDTO) {
        log.debug("REST request to create partner: {}", partnerDTO);
        PartnerDTO createdPartner = partnerService.createPartner(partnerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPartner);
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les partenaires", description = "Récupère tous les partenaires avec pagination")
    public ResponseEntity<Page<PartnerDTO>> getAllPartners(
            @PageableDefault(size = 20, sort = "alias") Pageable pageable) {
        log.debug("REST request to get all partners");
        Page<PartnerDTO> page = partnerService.getAllPartners(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un partenaire par ID", description = "Récupère un partenaire spécifique par son ID")
    public ResponseEntity<PartnerDTO> getPartnerById(
            @PathVariable @Parameter(description = "ID du partenaire") Long id) {
        log.debug("REST request to get partner with ID: {}", id);
        PartnerDTO partnerDTO = partnerService.getPartnerById(id);
        return ResponseEntity.ok(partnerDTO);
    }

    @GetMapping("/alias/{alias}")
    @Operation(summary = "Récupérer un partenaire par alias", description = "Récupère un partenaire spécifique par son alias")
    public ResponseEntity<PartnerDTO> getPartnerByAlias(
            @PathVariable @Parameter(description = "Alias du partenaire") String alias) {
        log.debug("REST request to get partner with alias: {}", alias);
        PartnerDTO partnerDTO = partnerService.getPartnerByAlias(alias);
        return ResponseEntity.ok(partnerDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un partenaire", description = "Met à jour un partenaire existant")
    public ResponseEntity<PartnerDTO> updatePartner(
            @PathVariable @Parameter(description = "ID du partenaire") Long id,
            @Valid @RequestBody PartnerDTO partnerDTO) {
        log.debug("REST request to update partner with ID: {}", id);
        PartnerDTO updatedPartner = partnerService.updatePartner(id, partnerDTO);
        return ResponseEntity.ok(updatedPartner);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un partenaire", description = "Supprime un partenaire existant")
    public ResponseEntity<Void> deletePartner(
            @PathVariable @Parameter(description = "ID du partenaire") Long id) {
        log.debug("REST request to delete partner with ID: {}", id);
        partnerService.deletePartner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des partenaires", description = "Recherche des partenaires par mot-clé")
    public ResponseEntity<Page<PartnerDTO>> searchPartners(
            @RequestParam @Parameter(description = "Mot-clé de recherche") String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search partners with keyword: {}", keyword);
        Page<PartnerDTO> page = partnerService.searchPartners(keyword, pageable);
        return ResponseEntity.ok(page);
    }
}