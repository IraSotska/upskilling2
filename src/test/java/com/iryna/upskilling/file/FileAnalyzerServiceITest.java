package com.iryna.upskilling.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FileAnalyzerServiceITest {

    private FileAnalyzerService fileAnalyzerService = new FileAnalyzerService();

    @DisplayName("Should Analyze File Contains Cyrillic Word")
    @Test
    void shouldAnalyzeFileContainsCyrillicWord() {
        var expectedSentences = List.of("Метод принимает класс и возвращает созданный объект этого класса.",
                "Метод принимает object и вызывает у него все методы без параметров!",
                "Метод принимает object и выводит на экран все сигнатуры методов в который есть final.",
                "Метод принимает Class и выводит все не публичные методы этого класса.",
                "Метод принимает Class и выводит всех предков класса и все интерфейсы которое класс имплементирует.",
                "Метод принимает объект и меняет всего его приватные поля на их нулевые значение (null, 0, false etc)+?");

        var result = fileAnalyzerService.analyze(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("sentences.txt")).getPath(), "Метод");

        assertEquals(9, result.getWordCount());
        assertEquals(expectedSentences, result.getSentences());
    }

    @DisplayName("Should Analyze File Contains English Word")
    @Test
    void shouldAnalyzeFileContainsEnglishWord() {
        var expectedSentences = List.of(
                "Метод принимает Class и выводит все не публичные методы этого класса.",
                "Метод принимает Class и выводит всех предков класса и все интерфейсы которое класс имплементирует.");

        var result = fileAnalyzerService.analyze(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("sentences.txt")).getPath(), "Class");

        assertEquals(2, result.getWordCount());
        assertEquals(expectedSentences, result.getSentences());
    }

    @DisplayName("Should Analyze File With Word That Not Exist")
    @Test
    void shouldAnalyzeFileWithWordThatNotExist() {
        var result = fileAnalyzerService.analyze(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("sentences.txt")).getPath(), "НЕМетод");

        assertEquals(0, result.getWordCount());
        assertEquals(List.of(), result.getSentences());
    }
}