package controllers;

import com.example.realize.controllers.TransferController;
import com.example.realize.services.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.UUID;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferService transferService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(transferController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(Jwt.class);
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter,
                                                  ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest,
                                                  WebDataBinderFactory binderFactory) {
                        return jwt;
                    }
                })
                .build();
    }

    @Test
    public void transferFunds_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        String mockSubjectId = "user-123-xyz";
        String idempotencyKey = UUID.randomUUID().toString();
        String outcomingAccountId = UUID.randomUUID().toString();

        when(jwt.getSubject()).thenReturn(mockSubjectId);

        String jsonRequest = String.format(
                "{\"outcomingAccountId\":\"%s\",\"amountToTransfer\":150.50}",
                outcomingAccountId
        );

        mockMvc.perform(post("/api/account/transfer")
                        .header("Idempotency-Key", idempotencyKey) // Passes the custom header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Transfer completed successfully!")));

        verify(transferService).transferFunds(
                eq(mockSubjectId),
                eq(outcomingAccountId),
                any(),
                eq(idempotencyKey)
        );
    }
}
