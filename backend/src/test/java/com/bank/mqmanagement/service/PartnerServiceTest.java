package com.bank.mqmanagement.service;

import com.bank.mqmanagement.dto.PartnerDTO;
import com.bank.mqmanagement.exception.ResourceNotFoundException;
import com.bank.mqmanagement.model.Partner;
import com.bank.mqmanagement.model.Partner.Direction;
import com.bank.mqmanagement.model.Partner.ProcessedFlowType;
import com.bank.mqmanagement.repository.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private PartnerService partnerService;

    private Partner partner1;
    private Partner partner2;
    private LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Initialiser les données de test
        partner1 = Partner.builder()
                .id(1L)
                .alias("PARTNER001")
                .type("BANK")
                .direction(Direction.INBOUND)
                .application("App1")
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("Test partner 1")
                .createdAt(now)
                .build();

        partner2 = Partner.builder()
                .id(2L)
                .alias("PARTNER002")
                .type("PAYMENT")
                .direction(Direction.OUTBOUND)
                .application("App2")
                .processedFlowType(ProcessedFlowType.NOTIFICATION)
                .description("Test partner 2")
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();
    }

    @Test
    void testGetAllPartners() {
        // Arrange
        List<Partner> partners = Arrays.asList(partner1, partner2);
        Page<Partner> partnerPage = new PageImpl<>(partners);

        when(partnerRepository.findAllByOrderByAliasAsc(any(Pageable.class)))
                .thenReturn(partnerPage);

        // Act
        Page<PartnerDTO> result = partnerService.getAllPartners(Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(partner1.getId(), result.getContent().get(0).getId());
        assertEquals(partner2.getId(), result.getContent().get(1).getId());

        verify(partnerRepository, times(1)).findAllByOrderByAliasAsc(any(Pageable.class));
    }

    @Test
    void testGetPartnerById_WhenExists() {
        // Arrange
        when(partnerRepository.findById(eq(1L))).thenReturn(Optional.of(partner1));

        // Act
        PartnerDTO result = partnerService.getPartnerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(partner1.getId(), result.getId());
        assertEquals(partner1.getAlias(), result.getAlias());
        assertEquals(partner1.getType(), result.getType());

        verify(partnerRepository, times(1)).findById(eq(1L));
    }

    @Test
    void testGetPartnerById_WhenNotExists() {
        // Arrange
        when(partnerRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            partnerService.getPartnerById(999L);
        });

        verify(partnerRepository, times(1)).findById(eq(999L));
    }

    @Test
    void testCreatePartner_Success() {
        // Arrange
        PartnerDTO partnerDTO = new PartnerDTO();
        partnerDTO.setAlias("NEW_PARTNER");
        partnerDTO.setType("BANK");
        partnerDTO.setDirection(Direction.INBOUND);
        partnerDTO.setProcessedFlowType(ProcessedFlowType.MESSAGE);
        partnerDTO.setDescription("New partner description");

        Partner savedPartner = Partner.builder()
                .id(3L)
                .alias("NEW_PARTNER")
                .type("BANK")
                .direction(Direction.INBOUND)
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("New partner description")
                .createdAt(now)
                .build();

        when(partnerRepository.existsByAlias(eq("NEW_PARTNER"))).thenReturn(false);
        when(partnerRepository.save(any(Partner.class))).thenReturn(savedPartner);

        // Act
        PartnerDTO result = partnerService.createPartner(partnerDTO);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("NEW_PARTNER", result.getAlias());

        verify(partnerRepository, times(1)).existsByAlias(eq("NEW_PARTNER"));
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    void testCreatePartner_DuplicateAlias() {
        // Arrange
        PartnerDTO partnerDTO = new PartnerDTO();
        partnerDTO.setAlias("PARTNER001");  // Alias already exists
        partnerDTO.setType("BANK");
        partnerDTO.setDirection(Direction.INBOUND);
        partnerDTO.setProcessedFlowType(ProcessedFlowType.MESSAGE);
        partnerDTO.setDescription("New partner description");

        when(partnerRepository.existsByAlias(eq("PARTNER001"))).thenReturn(true);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            partnerService.createPartner(partnerDTO);
        });

        verify(partnerRepository, times(1)).existsByAlias(eq("PARTNER001"));
        verify(partnerRepository, never()).save(any(Partner.class));
    }

    @Test
    void testDeletePartner() {
        // Arrange
        when(partnerRepository.existsById(eq(1L))).thenReturn(true);
        doNothing().when(partnerRepository).deleteById(eq(1L));

        // Act
        partnerService.deletePartner(1L);

        // Assert
        verify(partnerRepository, times(1)).existsById(eq(1L));
        verify(partnerRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    void testDeletePartner_NotFound() {
        // Arrange
        when(partnerRepository.existsById(eq(999L))).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            partnerService.deletePartner(999L);
        });

        verify(partnerRepository, times(1)).existsById(eq(999L));
        verify(partnerRepository, never()).deleteById(any());
    }
}