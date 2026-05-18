package com.wordfrequency.analyzer;

import java.util.List;

public interface WordFrequencyAnalyzer {
    int calculateHighestFrequency(String text);
    int calculateFrequencyForWord(String text, String word);
    List<WordFrequency> calculateMostFrequentNWords(String text, int n);
}
