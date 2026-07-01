package services;

import com.example.realize.dto.models.Account;
import com.example.realize.dto.responses.AccountGetResponse;
import com.example.realize.exceptions.ResourceNotFoundException;
import com.example.realize.repository.AccountRepository;
import com.example.realize.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void createAccount_ShouldSaveParsedAccountSuccess() {
        String name = "John Doe";
        UUID id = UUID.randomUUID();
        Integer startBalance = 1000;
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        accountService.createAccount(name, id, startBalance);

        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertEquals(id, savedAccount.getId());
        assertEquals(name, savedAccount.getName());
        assertEquals(startBalance, savedAccount.getBalance());
    }

    @Test
    public void getAccount_WhenAccountExists_ShouldReturnAccount() {
        UUID id = UUID.randomUUID();
        Account expectedAccount = new Account();
        expectedAccount.setId(id);
        expectedAccount.setName("Jane Doe");

        when(accountRepository.findById(id)).thenReturn(Optional.of(expectedAccount));

        Account actualAccount = accountService.getAccount(id);

        assertNotNull(actualAccount);
        assertEquals(id, actualAccount.getId());
        assertEquals("Jane Doe", actualAccount.getName());
        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    public void getAccount_WhenAccountDoesNotExist_ShouldThrowResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccount(id);
        });

        assertEquals("Account with id " + id + " does not exist", exception.getMessage());
        verify(accountRepository, times(1)).findById(id);
    }

    @Test
    public void parseAccountEntity_ShouldMapCorrectly() {
        UUID id = UUID.randomUUID();

        Account account = accountService.parseAccountEntity("Savings", id, 500);

        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals("Savings", account.getName());
        assertEquals(500, account.getBalance());
    }

    @Test
    public void parseAccountGetResponse_ShouldMapCorrectly() {
        UUID id = UUID.randomUUID();
        Account account = new Account();
        account.setId(id);
        account.setName("Checking");
        account.setBalance(250);

        AccountGetResponse response = accountService.parseAccountGetResponse(account);

        assertNotNull(response);
        assertEquals("Checking", response.getName());
        assertEquals(id, response.getId());
        assertEquals(250, response.getStartBalance());
    }

    @Test
    public void updateAccountBalance_WithSufficientFunds_ShouldModifyBalancesAndSaveBoth() {
        Account outcomingAccount = new Account();
        outcomingAccount.setId(UUID.randomUUID());
        outcomingAccount.setBalance(500);

        Account incomingAccount = new Account();
        incomingAccount.setId(UUID.randomUUID());
        incomingAccount.setBalance(200);

        accountService.updateAccountBalance(outcomingAccount, incomingAccount, 150);

        assertEquals(350, outcomingAccount.getBalance());
        assertEquals(350, incomingAccount.getBalance());

        verify(accountRepository, times(1)).save(outcomingAccount);
        verify(accountRepository, times(1)).save(incomingAccount);
    }

    @Test
    public void updateAccountBalance_WithInsufficientFunds_ShouldThrowIllegalStateExceptionAndNeverSave() {
        Account outcomingAccount = new Account();
        outcomingAccount.setId(UUID.randomUUID());
        outcomingAccount.setBalance(100);

        Account incomingAccount = new Account();
        incomingAccount.setId(UUID.randomUUID());
        incomingAccount.setBalance(200);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            accountService.updateAccountBalance(outcomingAccount, incomingAccount, 150);
        });

        assertEquals("Insufficient funds for this transfer", exception.getMessage());

        assertEquals(100, outcomingAccount.getBalance());
        assertEquals(200, incomingAccount.getBalance());

        verify(accountRepository, never()).save(any(Account.class));
    }
}
