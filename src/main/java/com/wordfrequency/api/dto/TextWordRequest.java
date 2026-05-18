package com.wordfrequency.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TextWordRequest(
        @NotNull String text,
        @NotBlank String word) {
}
