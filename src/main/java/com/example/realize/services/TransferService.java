package com.example.realize.services;

import com.example.realize.dto.models.Account;
import com.example.realize.dto.models.Transfer;
import com.example.realize.dto.models.TransferStatus;
import com.example.realize.repository.AccountRepository;
import com.example.realize.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final AccountService accountService;
    private final CacheService cacheService;

    public void transferFunds(
            String incomingAccountId,
            String outcomingAccountId,
            Integer amountToTransfer,
            String idempotencyKey
    ) {
        var cachedTransfer = cacheService.set(idempotencyKey, "", 1);

        if(cachedTransfer != null) return;

        Account outcomingAccount = accountService.getAccount(UUID.fromString(outcomingAccountId));
        Account incomingAccount = accountService.getAccount(UUID.fromString(incomingAccountId));

        Transfer transfer = new Transfer();
        transfer.setId(UUID.randomUUID());
        transfer.setAmount(amountToTransfer);
        transfer.setOutcomingAccount(outcomingAccount);
        transfer.setIncomingAccount(incomingAccount);
        transfer.setStatus(TransferStatus.processing);
        transferRepository.save(transfer);

        try {
            accountService.updateAccountBalance(incomingAccount, outcomingAccount, amountToTransfer);

            transfer.setStatus(TransferStatus.done);
        } catch (Exception e) {
            transfer.setStatus(TransferStatus.error);
        }

        transferRepository.save(transfer);
        this.sendNotification();
    }

    public void sendNotification() {
        // send the notification to a queue and process it individually, this way the message could be sent with the
        // preferred rule for the company
    }
}
