package com.wordfrequency.api.dto;

import jakarta.validation.constraints.NotNull;

public record TextRequest(@NotNull String text) {
}
