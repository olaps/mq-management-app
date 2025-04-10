package com.bank.mqmanagement.repository;

import com.bank.mqmanagement.model.Partner;
import com.bank.mqmanagement.model.Partner.Direction;
import com.bank.mqmanagement.model.Partner.ProcessedFlowType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PartnerRepositoryTest {

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    void testFindByAlias() {
        // Arrange
        String alias = "TEST-PARTNER";
        LocalDateTime now = LocalDateTime.now();

        Partner partner = Partner.builder()
                .alias(alias)
                .type("BANK")
                .direction(Direction.INBOUND)
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("Test partner")
                .createdAt(now)
                .build();

        partnerRepository.save(partner);

        // Act
        Optional<Partner> result = partnerRepository.findByAlias(alias);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(alias, result.get().getAlias());
        assertEquals("BANK", result.get().getType());
        assertEquals(Direction.INBOUND, result.get().getDirection());
    }

    @Test
    void testExistsByAlias() {
        // Arrange
        String alias = "TEST-PARTNER";
        LocalDateTime now = LocalDateTime.now();

        Partner partner = Partner.builder()
                .alias(alias)
                .type("BANK")
                .direction(Direction.INBOUND)
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("Test partner")
                .createdAt(now)
                .build();

        partnerRepository.save(partner);

        // Act
        boolean exists = partnerRepository.existsByAlias(alias);
        boolean notExists = partnerRepository.existsByAlias("NON-EXISTENT");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindAllByOrderByAliasAsc() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Partner partner1 = Partner.builder()
                .alias("B-PARTNER")
                .type("BANK")
                .direction(Direction.INBOUND)
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("Test partner B")
                .createdAt(now)
                .build();

        Partner partner2 = Partner.builder()
                .alias("A-PARTNER")
                .type("PAYMENT")
                .direction(Direction.OUTBOUND)
                .processedFlowType(ProcessedFlowType.NOTIFICATION)
                .description("Test partner A")
                .createdAt(now)
                .build();

        partnerRepository.save(partner1);
        partnerRepository.save(partner2);

        // Act
        Page<Partner> result = partnerRepository.findAllByOrderByAliasAsc(PageRequest.of(0, 10));

        // Assert
        assertEquals(2, result.getTotalElements());
        // Le premier partenaire doit être A-PARTNER (ordre alphabétique)
        assertEquals("A-PARTNER", result.getContent().get(0).getAlias());
        assertEquals("B-PARTNER", result.getContent().get(1).getAlias());
    }

    @Test
    void testFindByDirection() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Partner partner1 = Partner.builder()
                .alias("PARTNER-1")
                .type("BANK")
                .direction(Direction.INBOUND)
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("Test partner 1")
                .createdAt(now)
                .build();

        Partner partner2 = Partner.builder()
                .alias("PARTNER-2")
                .type("PAYMENT")
                .direction(Direction.OUTBOUND)
                .processedFlowType(ProcessedFlowType.NOTIFICATION)
                .description("Test partner 2")
                .createdAt(now)
                .build();

        partnerRepository.save(partner1);
        partnerRepository.save(partner2);

        // Act
        List<Partner> inboundResult = partnerRepository.findByDirection(Direction.INBOUND);
        List<Partner> outboundResult = partnerRepository.findByDirection(Direction.OUTBOUND);

        // Assert
        assertEquals(1, inboundResult.size());
        assertEquals("PARTNER-1", inboundResult.get(0).getAlias());

        assertEquals(1, outboundResult.size());
        assertEquals("PARTNER-2", outboundResult.get(0).getAlias());
    }

    @Test
    void testSearch() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Partner partner1 = Partner.builder()
                .alias("BANK-PARTNER")
                .type("FINANCIAL")
                .application("Banking App")
                .direction(Direction.INBOUND)
                .processedFlowType(ProcessedFlowType.MESSAGE)
                .description("A financial institution partner")
                .createdAt(now)
                .build();

        Partner partner2 = Partner.builder()
                .alias("PAYMENT-PARTNER")
                .type("PAYMENT")
                .application("Payment Gateway")
                .direction(Direction.OUTBOUND)
                .processedFlowType(ProcessedFlowType.NOTIFICATION)
                .description("A payment processing partner")
                .createdAt(now)
                .build();

        partnerRepository.save(partner1);
        partnerRepository.save(partner2);

        // Act
        Page<Partner> result1 = partnerRepository.search("BANK", PageRequest.of(0, 10));
        Page<Partner> result2 = partnerRepository.search("payment", PageRequest.of(0, 10));
        Page<Partner> result3 = partnerRepository.search("financial", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result1.getTotalElements());
        assertEquals("BANK-PARTNER", result1.getContent().get(0).getAlias());

        assertEquals(1, result2.getTotalElements());
        assertEquals("PAYMENT-PARTNER", result2.getContent().get(0).getAlias());

        assertEquals(1, result3.getTotalElements());
        assertEquals("BANK-PARTNER", result3.getContent().get(0).getAlias());
    }
}