package com.iryna.upskilling.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileAnalyzerServiceTest {

    private FileAnalyzerService fileAnalyzerService = new FileAnalyzerService();

    @DisplayName("Should Get File Content")
    @Test
    void shouldGetFileContent() {
        var someString = "Should Get File Content";

        assertEquals(someString, fileAnalyzerService.getFileContent(new ByteArrayInputStream(someString.getBytes())));
    }

    @DisplayName("Should Split Content")
    @Test
    void shouldSplitContent() {
        var resultSentences = List.of("Should Get File Content.", "Should Get File Content2.", "Should Get File Content3!");
        var someString = "Should Get File Content.\r\nShould Get File Content2.\r\nShould Get File Content3!";

        assertEquals(resultSentences, fileAnalyzerService.splitContent(someString));
    }

    @DisplayName("Should Filter Sentences Contain Word")
    @Test
    void shouldFilterSentencesContainWord() {
        var resultSentences = List.of("Should Get File Content.", "Should Get File Content3!");
        var sentences = List.of("Should Get File Content.", "Should Get File.", "Should Get File Content3!");

        assertEquals(resultSentences, fileAnalyzerService.filterSentencesContainWord(sentences, "Content"));
    }

    @DisplayName("Should Get Count Of Word")
    @Test
    void shouldGetCountOfWord() {
        var sentences = List.of("Should Get File Content.", "Should Get File.", "Should Get File Content3!");

        assertEquals(2, fileAnalyzerService.getCountOfWord(sentences, "Content"));
    }
}