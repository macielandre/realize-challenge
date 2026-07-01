package controllers;

import com.example.realize.controllers.AccountController;
import com.example.realize.dto.models.Account;
import com.example.realize.dto.responses.AccountGetResponse;
import com.example.realize.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountController)
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
    public void createAccount_ShouldReturnCreated() throws Exception {
        String jsonRequest = "{\"name\":\"Jane Doe\",\"startBalance\":500.00}";

        mockMvc.perform(post("/api/public/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("created successfully!")));

        verify(accountService).createAccount(eq("Jane Doe"), any(UUID.class), any());
    }

    @Test
    public void getAccount_ShouldReturnAccountDetails() throws Exception {
        UUID userId = UUID.randomUUID();
        Account mockAccount = Mockito.mock(Account.class);
        AccountGetResponse mockResponse = Mockito.mock(AccountGetResponse.class);

        when(jwt.getSubject()).thenReturn(userId.toString());

        when(accountService.getAccount(userId)).thenReturn(mockAccount);
        when(accountService.parseAccountGetResponse(mockAccount)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/account"))
                .andExpect(status().isOk());

        verify(accountService).getAccount(userId);
        verify(accountService).parseAccountGetResponse(mockAccount);
    }
}