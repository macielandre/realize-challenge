package com.example.realize.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import org.hibernate.validator.constraints.UUID;

@Getter
public class TransferRequestDto {
    @NotNull(message = "The amountToTransfer must be provided.")
    @PositiveOrZero(message = "The amount to transfer cannot be negative.")
    private Integer amountToTransfer;

    @NotNull(message = "The outcomingAccountId must be provided.")
    @UUID(message = "The provided string must be a valid formatted UUID.")
    private String outcomingAccountId;
}
