package com.example.realize.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

@RestController
@RequestMapping("/api/public/token")
public class TokenController {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("")
    public ResponseEntity<String> getToken(@RequestParam String name, @RequestParam String id) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return ResponseEntity.status(HttpStatus.CREATED).body(JWT.create()
                .withSubject(id)
                .withClaim("name", name)
                .withIssuedAt(new Date())
                .sign(algorithm));
    }
}
