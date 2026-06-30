package com.example.realize.controllers;

import com.example.realize.dto.models.Account;
import com.example.realize.dto.requests.AccountCreationRequestDto;
import com.example.realize.dto.responses.AccountGetResponse;
import com.example.realize.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Account Actions", description = "Endpoints for managing accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/public/account")
    @Operation(summary = "Create a new bank account", description = "Allows public registration of an account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account successfully created"),
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    public ResponseEntity<String> createAccount(@Valid @RequestBody AccountCreationRequestDto request) {
        UUID id = UUID.randomUUID();
        accountService.createAccount(
                request.getName(),
                id,
                request.getStartBalance()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("Account '" + id + "' created successfully!");
    }

    @GetMapping("/account")
    @Operation(summary = "Get account details", description = "Retrieves information for the authenticated user.")
    public ResponseEntity<AccountGetResponse> getAccount(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Account account = accountService.getAccount(UUID.fromString(jwt.getSubject()));
        AccountGetResponse response = accountService.parseAccountGetResponse(account);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}