package com.example.realize.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransferService {
    public void transferFunds(
            String incomingAccountId,
            String outcomingAccountId,
            Integer amountToTransfer,
            String idempotencyKey
    ) {
        return;
    }
}
