package com.example.realize.controllers;

import com.example.realize.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

@RestController
@RequestMapping("/api/public/token")
@Tag(name = "Token Actions", description = "Endpoints for managing a mock token")
@RequiredArgsConstructor
public class TokenController {
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    private final AccountService accountService;

    @GetMapping("")
    @Operation(summary = "Get a token", description = "Get a mock token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token successfully returned"),
    })
    @io.swagger.v3.oas.annotations.security.SecurityRequirements
    public ResponseEntity<String> getToken(@RequestParam String id) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        var name = accountService.getAccount(java.util.UUID.fromString(id)).getName();
        return ResponseEntity.status(HttpStatus.OK).body(JWT.create()
                .withSubject(id)
                .withClaim("name", name)
                .withIssuedAt(new Date())
                .sign(algorithm));
    }
}
