package com.example.realize.services;

import com.example.realize.dto.models.Account;
import com.example.realize.dto.responses.AccountGetResponse;
import com.example.realize.exceptions.ResourceNotFoundException;
import com.example.realize.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

    public AccountGetResponse getAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + id + " does not exist"));
        return this.parseAccountGetResponse(account.getName(), account.getId(), account.getBalance());
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

    public AccountGetResponse parseAccountGetResponse(
            String name,
            UUID id,
            Integer balance
    ) {
        return new AccountGetResponse(name, id, balance);
    }
}
