package com.bank.mqmanagement.controller;

import com.bank.mqmanagement.dto.PartnerDTO;
import com.bank.mqmanagement.model.Partner.Direction;
import com.bank.mqmanagement.model.Partner.ProcessedFlowType;
import com.bank.mqmanagement.service.PartnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PartnerController.class)
public class PartnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnerService partnerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePartner() throws Exception {
        // Arrange
        PartnerDTO partnerDTO = createSamplePartner(null, "PARTNER001");
        PartnerDTO createdPartner = createSamplePartner(1L, "PARTNER001");

        when(partnerService.createPartner(any(PartnerDTO.class))).thenReturn(createdPartner);

        // Act & Assert
        mockMvc.perform(post("/partners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partnerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.alias").value("PARTNER001"));
    }

    @Test
    void testGetAllPartners() throws Exception {
        // Arrange
        PartnerDTO partner1 = createSamplePartner(1L, "PARTNER001");
        PartnerDTO partner2 = createSamplePartner(2L, "PARTNER002");

        when(partnerService.getAllPartners(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(partner1, partner2)));

        // Act & Assert
        mockMvc.perform(get("/partners")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].alias").value("PARTNER001"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].alias").value("PARTNER002"));
    }

    @Test
    void testDeletePartner() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(partnerService).deletePartner(eq(id));

        // Act & Assert
        mockMvc.perform(delete("/partners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdatePartner() throws Exception {
        // Arrange
        Long id = 1L;
        PartnerDTO partnerDTO = createSamplePartner(id, "PARTNER001-UPDATED");

        when(partnerService.updatePartner(eq(id), any(PartnerDTO.class))).thenReturn(partnerDTO);

        // Act & Assert
        mockMvc.perform(put("/partners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partnerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.alias").value("PARTNER001-UPDATED"));
    }

    private PartnerDTO createSamplePartner(Long id, String alias) {
        PartnerDTO partner = new PartnerDTO();
        partner.setId(id);
        partner.setAlias(alias);
        partner.setType("BANK");
        partner.setDirection(Direction.INBOUND);
        partner.setProcessedFlowType(ProcessedFlowType.MESSAGE);
        partner.setDescription("Test partner description");
        return partner;
    }
}