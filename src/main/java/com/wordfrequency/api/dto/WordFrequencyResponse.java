package com.wordfrequency.api.dto;

import com.wordfrequency.analyzer.WordFrequency;

public record WordFrequencyResponse(String word, int frequency) {

    public static WordFrequencyResponse from(WordFrequency source) {
        return new WordFrequencyResponse(source.getWord(), source.getFrequency());
    }
}
