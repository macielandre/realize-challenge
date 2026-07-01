package services;
import com.example.realize.dto.models.Account;
import com.example.realize.dto.models.Transfer;
import com.example.realize.dto.models.TransferStatus;
import com.example.realize.repository.TransferRepository;
import com.example.realize.services.AccountService;
import com.example.realize.services.CacheService;
import com.example.realize.services.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    @Spy
    private TransferService transferService;

    @Test
    public void transferFunds_WhenIdempotencyKeyExistsInCache_ShouldReturnEarly() {
        String idempotencyKey = UUID.randomUUID().toString();
        when(cacheService.set(idempotencyKey, "", 1L)).thenReturn(false);

        transferService.transferFunds(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                500,
                idempotencyKey
        );

        verify(cacheService, times(1)).set(idempotencyKey, "", 1L);
        verifyNoInteractions(accountService);
        verifyNoInteractions(transferRepository);
        verify(transferService, never()).sendNotification();
    }

    @Test
    public void transferFunds_WhenExecutionIsSuccessful_ShouldSaveWithDoneStatus() {
        String idempotencyKey = UUID.randomUUID().toString();
        UUID incomingId = UUID.randomUUID();
        UUID outcomingId = UUID.randomUUID();
        Integer amount = 300;

        Account mockIncoming = new Account();
        Account mockOutcoming = new Account();

        when(cacheService.set(idempotencyKey, "", 1L)).thenReturn(true);
        when(accountService.getAccount(incomingId)).thenReturn(mockIncoming);
        when(accountService.getAccount(outcomingId)).thenReturn(mockOutcoming);

        List<TransferStatus> savedStatuses = new ArrayList<>();
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            savedStatuses.add(transfer.getStatus());
            return transfer;
        });

        transferService.transferFunds(
                incomingId.toString(),
                outcomingId.toString(),
                amount,
                idempotencyKey
        );

        verify(accountService, times(1)).updateAccountBalance(mockIncoming, mockOutcoming, amount);
        verify(transferRepository, times(2)).save(any(Transfer.class));

        assertEquals(2, savedStatuses.size());
        assertEquals(TransferStatus.processing, savedStatuses.get(0));
        assertEquals(TransferStatus.done, savedStatuses.get(1));

        verify(transferService, times(1)).sendNotification();
    }

    @Test
    public void transferFunds_WhenBalanceUpdateThrowsException_ShouldSaveWithErrorStatus() {
        String idempotencyKey = UUID.randomUUID().toString();
        UUID incomingId = UUID.randomUUID();
        UUID outcomingId = UUID.randomUUID();
        Integer amount = 1000;

        Account mockIncoming = new Account();
        Account mockOutcoming = new Account();

        when(cacheService.set(idempotencyKey, "", 1L)).thenReturn(true);
        when(accountService.getAccount(incomingId)).thenReturn(mockIncoming);
        when(accountService.getAccount(outcomingId)).thenReturn(mockOutcoming);
        doThrow(new RuntimeException("Inbound balance mismatch")).when(accountService)
                .updateAccountBalance(any(), any(), any());

        List<TransferStatus> savedStatuses = new ArrayList<>();
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer transfer = invocation.getArgument(0);
            savedStatuses.add(transfer.getStatus());
            return transfer;
        });

        transferService.transferFunds(
                incomingId.toString(),
                outcomingId.toString(),
                amount,
                idempotencyKey
        );

        verify(transferRepository, times(2)).save(any(Transfer.class));

        assertEquals(2, savedStatuses.size());
        assertEquals(TransferStatus.processing, savedStatuses.get(0));
        assertEquals(TransferStatus.error, savedStatuses.get(1));

        verify(transferService, times(1)).sendNotification();
    }
}