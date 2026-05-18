package com.wordfrequency.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import com.wordfrequency.analyzer.WordFrequencyAnalyzer;
import com.wordfrequency.api.dto.FrequencyResponse;
import com.wordfrequency.api.dto.TextRequest;
import com.wordfrequency.api.dto.TextWordRequest;
import com.wordfrequency.api.dto.WordFrequencyResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/word-frequency")
@Validated
public class WordFrequencyController {

    private final WordFrequencyAnalyzer analyzer;

    public WordFrequencyController(WordFrequencyAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @PostMapping("/highest-frequency")
    public FrequencyResponse highestFrequency(@Valid @RequestBody TextRequest request) {
        return new FrequencyResponse(analyzer.calculateHighestFrequency(request.text()));
    }

    @PostMapping("/frequency-for-word")
    public FrequencyResponse frequencyForWord(@Valid @RequestBody TextWordRequest request) {
        return new FrequencyResponse(analyzer.calculateFrequencyForWord(request.text(), request.word()));
    }

    @PostMapping("/most-frequent-words")
    public List<WordFrequencyResponse> mostFrequentWords(
            @Valid @RequestBody TextRequest request,
            @RequestParam @Positive int n) {
        return analyzer.calculateMostFrequentNWords(request.text(), n).stream()
                .map(WordFrequencyResponse::from)
                .toList();
    }
}
