package services;

import com.example.realize.services.CacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheService cacheService;

    @Test
    public void set_ShouldCallSetIfAbsentAndReturnResult() {
        String key = "idempotency:key-123";
        String value = "processing";
        long timeout = 10L;
        Boolean expectedResult = true;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(key, value, Duration.ofMinutes(timeout))).thenReturn(expectedResult);

        Object actualResult = cacheService.set(key, value, timeout);

        assertEquals(expectedResult, actualResult);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).setIfAbsent(key, value, Duration.ofMinutes(timeout));
    }
}