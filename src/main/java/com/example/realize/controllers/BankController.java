package com.example.realize.controllers;

import com.example.realize.dto.requests.AccountRequestDto;
import com.example.realize.dto.requests.TransferRequestDto;
import com.example.realize.services.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/public/account")
    public ResponseEntity<String> createAccount(@RequestBody AccountRequestDto request) {
        bankService.createAccount();
        return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully!");
    }

    @GetMapping("/account")
    public ResponseEntity<String> getAccount(
            @AuthenticationPrincipal Jwt jwt
    ) {
        bankService.getAccount();
        return ResponseEntity.ok("Account data for ID: " + jwt.getSubject());
    }

    @PostMapping("/account/transfer")
    public ResponseEntity<String> transferFunds(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody TransferRequestDto request
    ) {
        bankService.transferFunds(
                jwt.getId(),
                request.getOutcomingId(),
                request.getAmountToTransfer()
        );
        return ResponseEntity.ok("Transfer completed successfully!");
    }
}