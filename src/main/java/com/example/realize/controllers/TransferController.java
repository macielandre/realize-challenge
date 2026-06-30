package com.example.realize.controllers;

import com.example.realize.dto.requests.TransferRequestDto;
import com.example.realize.services.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Transfer Account Actions", description = "Endpoints for managing accounts transfers")
@AllArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/account/transfer")
    @Operation(
            summary = "Transfer funds",
            description = "Transfers money to another account.",
            parameters = {
                    @Parameter(
                            name = "Idempotency-Key",
                            in = ParameterIn.HEADER,
                            description = "Unique UUID string to prevent duplicate transfer processing",
                            required = true,
                            schema = @Schema(type = "string", format = "uuid")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid JWT"),
    })
    public ResponseEntity<String> transferFunds(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequestDto request
    ) {
        transferService.transferFunds(
                jwt.getId(),
                request.getOutcomingAccountId(),
                request.getAmountToTransfer(),
                idempotencyKey
        );
        return ResponseEntity.ok("Transfer completed successfully!");
    }
}