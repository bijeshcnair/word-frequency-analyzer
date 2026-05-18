package com.wordfrequency.analyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DefaultWordFrequencyAnalyzerTest {

    private final WordFrequencyAnalyzer analyzer = new DefaultWordFrequencyAnalyzer();

    @Nested
    @DisplayName("calculateHighestFrequency")
    class HighestFrequency {

        @Test
        void returns_count_of_the_most_frequent_word() {
            assertThat(analyzer.calculateHighestFrequency("The car is the color purple.")).isEqualTo(2);
        }

        @Test
        void returns_zero_for_text_without_words() {
            assertThat(analyzer.calculateHighestFrequency("   ---  ??? 123 ")).isZero();
        }

        @Test
        void returns_zero_for_null_or_blank_input() {
            assertThat(analyzer.calculateHighestFrequency(null)).isZero();
            assertThat(analyzer.calculateHighestFrequency("")).isZero();
        }

        @Test
        void treats_digits_and_punctuation_as_separators() {
            // "a1a2a" -> three occurrences of "a"
            assertThat(analyzer.calculateHighestFrequency("a1a2a")).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("calculateFrequencyForWord")
    class FrequencyForWord {

        @Test
        void counts_occurrences_case_insensitively() {
            assertThat(analyzer.calculateFrequencyForWord("The car is the color purple.", "the")).isEqualTo(2);
            assertThat(analyzer.calculateFrequencyForWord("The car is the color purple.", "THE")).isEqualTo(2);
        }

        @Test
        void returns_zero_when_word_is_missing() {
            assertThat(analyzer.calculateFrequencyForWord("hello world", "insurance")).isZero();
        }

        @Test
        void returns_zero_for_null_or_empty_word() {
            assertThat(analyzer.calculateFrequencyForWord("hello", null)).isZero();
            assertThat(analyzer.calculateFrequencyForWord("hello", "")).isZero();
        }

        @Test
        void returns_zero_for_null_text() {
            assertThat(analyzer.calculateFrequencyForWord(null, "the")).isZero();
        }

        @Test
        void only_matches_full_word_tokens_not_substrings() {
            assertThat(analyzer.calculateFrequencyForWord("the theatre", "the")).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("calculateMostFrequentNWords")
    class MostFrequentNWords {

        @Test
        void returns_top_n_ordered_by_frequency_then_alphabetically() {
            // Spec example text. The spec's own sample output {("the",2),("cat",1),("walks",1)}
            // contradicts its own "alphabetical order" tiebreak rule (over < staircase < walks);
            // this test follows the explicit rule, not the illustrative example.
            List<WordFrequency> result = analyzer.calculateMostFrequentNWords("The cat walks over the staircase", 3);

            assertThat(result)
                    .extracting(WordFrequency::getWord, WordFrequency::getFrequency)
                    .containsExactly(
                            tuple("the", 2),
                            tuple("cat", 1),
                            tuple("over", 1));
        }

        @Test
        void breaks_ties_alphabetically() {
            List<WordFrequency> result = analyzer.calculateMostFrequentNWords("banana apple cherry", 3);

            assertThat(result)
                    .extracting(WordFrequency::getWord)
                    .containsExactly("apple", "banana", "cherry");
        }

        @Test
        void returns_lowercase_words() {
            List<WordFrequency> result = analyzer.calculateMostFrequentNWords("Hello HELLO hello", 1);

            assertThat(result).singleElement()
                    .extracting(WordFrequency::getWord, WordFrequency::getFrequency)
                    .containsExactly("hello", 3);
        }

        @Test
        void caps_at_available_unique_words_when_n_exceeds_them() {
            List<WordFrequency> result = analyzer.calculateMostFrequentNWords("a b c", 10);
            assertThat(result).hasSize(3);
        }

        @Test
        void returns_empty_list_for_non_positive_n() {
            assertThat(analyzer.calculateMostFrequentNWords("a b c", 0)).isEmpty();
            assertThat(analyzer.calculateMostFrequentNWords("a b c", -1)).isEmpty();
        }

        @Test
        void returns_empty_list_for_null_or_blank_text() {
            assertThat(analyzer.calculateMostFrequentNWords(null, 3)).isEmpty();
            assertThat(analyzer.calculateMostFrequentNWords("", 3)).isEmpty();
            assertThat(analyzer.calculateMostFrequentNWords("--- !!! 123", 3)).isEmpty();
        }
    }
}
