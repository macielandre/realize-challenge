package com.example.realize.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class AccountGetResponse {
    private String name;
    private UUID id;
    private Integer startBalance;
}
