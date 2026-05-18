package com.wordfrequency.analyzer;

public record WordCount(String word, int frequency) implements WordFrequency {

    public WordCount {
        if (word == null) {
            throw new IllegalArgumentException("word must not be null");
        }
        if (frequency < 0) {
            throw new IllegalArgumentException("frequency must not be negative");
        }
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }
}
