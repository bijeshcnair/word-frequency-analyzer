package com.wordfrequency.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record TextWordRequest(
        @NotNull String text,
        @NotBlank
        @Pattern(regexp = "[a-zA-Z]+", message = "must contain only letters a-z or A-Z")
        String word) {
}
