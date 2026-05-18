package com.wordfrequency.analyzer;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * Word tokens are sequences of one or more ASCII letters; comparisons are case-insensitive.
 * Implementation is stateless and safe to share across threads.
 */
@Service
public class DefaultWordFrequencyAnalyzer implements WordFrequencyAnalyzer {

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z]+");

    @Override
    public int calculateHighestFrequency(String text) {
        return countWords(text).values().stream()
                .mapToInt(Long::intValue)
                .max()
                .orElse(0);
    }

    @Override
    public int calculateFrequencyForWord(String text, String word) {
        if (word == null || word.isEmpty()) {
            return 0;
        }
        String target = word.toLowerCase(Locale.ROOT);
        return (int) tokenize(text).filter(target::equals).count();
    }

    @Override
    public List<WordFrequency> calculateMostFrequentNWords(String text, int n) {
        if (n <= 0) {
            return List.of();
        }
        return countWords(text).entrySet().stream()
                .sorted(WORD_FREQUENCY_ORDER)
                .limit(n)
                .<WordFrequency>map(entry -> new WordCount(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }

    private static final Comparator<Map.Entry<String, Long>> WORD_FREQUENCY_ORDER =
            Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue)
                    .reversed()
                    .thenComparing(Map.Entry::getKey);

    private Map<String, Long> countWords(String text) {
        return tokenize(text).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Stream<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return Stream.empty();
        }
        return WORD_PATTERN.matcher(text).results()
                .map(MatchResult::group)
                .map(s -> s.toLowerCase(Locale.ROOT));
    }
}
