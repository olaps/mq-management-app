
package com.bank.mqmanagement.dto;

import com.bank.mqmanagement.model.Partner.Direction;
import com.bank.mqmanagement.model.Partner.ProcessedFlowType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDTO {
    private Long id;

    @NotBlank(message = "L'alias est obligatoire")
    private String alias;

    @NotBlank(message = "Le type est obligatoire")
    private String type;

    @NotNull(message = "La direction est obligatoire")
    private Direction direction;

    private String application;

    @NotNull(message = "Le type de flux traité est obligatoire")
    private ProcessedFlowType processedFlowType;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}