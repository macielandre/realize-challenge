package com.example.realize.services;

import com.example.realize.dto.models.Account;
import com.example.realize.dto.responses.AccountGetResponse;
import com.example.realize.exceptions.ResourceNotFoundException;
import com.example.realize.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public void createAccount(
            String name,
            UUID id,
            Integer startBalance
    ) {
        Account account = this.parseAccountEntity(name, id, startBalance);
        accountRepository.save(account);
    }

    public Account getAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + id + " does not exist"));
    }

    public Account parseAccountEntity(
            String name,
            UUID id,
            Integer startBalance
    ) {
        Account account = new Account();
        account.setId(id);
        account.setName(name);
        account.setBalance(startBalance);
        return account;
    }

    public AccountGetResponse parseAccountGetResponse(Account account) {
        return new AccountGetResponse(account.getName(), account.getId(), account.getBalance());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAccountBalance(
            Account outcomingAccount,
            Account incomingAccount,
            Integer amountToTransfer
    ) {
        if (outcomingAccount.getBalance() < amountToTransfer) {
            throw new IllegalStateException("Insufficient funds for this transfer");
        }

        outcomingAccount.setBalance(outcomingAccount.getBalance() - amountToTransfer);
        incomingAccount.setBalance(incomingAccount.getBalance() + amountToTransfer);

        accountRepository.save(outcomingAccount);
        accountRepository.save(incomingAccount);
    }
}
