package com.example.realize.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AccountCreationRequestDto {
    @NotNull(message = "The name must be provided.")
    @Size(min = 2, max = 150, message = "The name must be between 2 and 150 characters.")
    private String name;
    @NotNull(message = "The startBalance must be provided.")
    @PositiveOrZero(message = "The startBalance cannot be negative.")
    private Integer startBalance;
}
