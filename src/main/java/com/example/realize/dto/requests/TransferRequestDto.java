package com.example.realize.dto.requests;

import lombok.Getter;

@Getter
public class TransferRequestDto {
    private Integer amountToTransfer;
    private String outcomingId;
}
