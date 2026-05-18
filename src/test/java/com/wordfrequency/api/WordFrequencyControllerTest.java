package com.wordfrequency.api;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import com.wordfrequency.analyzer.WordCount;
import com.wordfrequency.analyzer.WordFrequency;
import com.wordfrequency.analyzer.WordFrequencyAnalyzer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WordFrequencyController.class)
class WordFrequencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WordFrequencyAnalyzer analyzer;

    @Test
    void highest_frequency_endpoint_returns_analyzer_result() throws Exception {
        Mockito.when(analyzer.calculateHighestFrequency("The cat the")).thenReturn(2);

        mockMvc.perform(post("/api/word-frequency/highest-frequency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"The cat the\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value(2));
    }

    @Test
    void frequency_for_word_rejects_non_letter_word() throws Exception {
        mockMvc.perform(post("/api/word-frequency/frequency-for-word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"hello\",\"word\":\"hello!\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void highest_frequency_rejects_missing_text() throws Exception {
        mockMvc.perform(post("/api/word-frequency/highest-frequency")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void frequency_for_word_endpoint_returns_count() throws Exception {
        Mockito.when(analyzer.calculateFrequencyForWord("hello world hello", "hello")).thenReturn(2);

        mockMvc.perform(post("/api/word-frequency/frequency-for-word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"hello world hello\",\"word\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value(2));
    }

    @Test
    void frequency_for_word_rejects_blank_word() throws Exception {
        mockMvc.perform(post("/api/word-frequency/frequency-for-word")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"hello\",\"word\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void most_frequent_words_endpoint_returns_ranked_list() throws Exception {
        List<WordFrequency> ranked = List.of(
                new WordCount("the", 2),
                new WordCount("cat", 1),
                new WordCount("over", 1));
        Mockito.when(analyzer.calculateMostFrequentNWords(anyString(), anyInt())).thenReturn(ranked);

        mockMvc.perform(post("/api/word-frequency/most-frequent-words")
                        .param("n", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"The cat walks over the staircase\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].word").value("the"))
                .andExpect(jsonPath("$[0].frequency").value(2))
                .andExpect(jsonPath("$[1].word").value("cat"))
                .andExpect(jsonPath("$[2].word").value("over"));
    }

    @Test
    void most_frequent_words_rejects_non_positive_n() throws Exception {
        mockMvc.perform(post("/api/word-frequency/most-frequent-words")
                        .param("n", "0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"hello\"}"))
                .andExpect(status().isBadRequest());
    }
}
