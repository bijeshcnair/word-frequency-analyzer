package com.wordfrequency;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import com.wordfrequency.api.dto.FrequencyResponse;
import com.wordfrequency.api.dto.TextRequest;
import com.wordfrequency.api.dto.TextWordRequest;
import com.wordfrequency.api.dto.WordFrequencyResponse;

/**
 * Full-stack test: real Spring context, real HTTP, real analyzer.
 * Verifies that the wired application behaves end-to-end on the spec example.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class WordFrequencyApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void highest_frequency_against_running_app() {
        ResponseEntity<FrequencyResponse> response = rest.postForEntity(
                url("/api/word-frequency/highest-frequency"),
                new TextRequest("The car is the color purple."),
                FrequencyResponse.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().frequency()).isEqualTo(2);
    }

    @Test
    void frequency_for_word_against_running_app() {
        ResponseEntity<FrequencyResponse> response = rest.postForEntity(
                url("/api/word-frequency/frequency-for-word"),
                new TextWordRequest("The car is the color purple.", "the"),
                FrequencyResponse.class);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().frequency()).isEqualTo(2);
    }

    @Test
    void most_frequent_words_against_running_app() {
        ResponseEntity<WordFrequencyResponse[]> response = rest.postForEntity(
                url("/api/word-frequency/most-frequent-words?n=3"),
                new TextRequest("The cat walks over the staircase"),
                WordFrequencyResponse[].class);

        assertThat(response.getBody())
                .extracting(WordFrequencyResponse::word, WordFrequencyResponse::frequency)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("the", 2),
                        org.assertj.core.groups.Tuple.tuple("cat", 1),
                        org.assertj.core.groups.Tuple.tuple("over", 1));
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
