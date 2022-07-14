package com.iryna.upskilling.file;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileAnalyzerService {

    public FileInformation analyze(String path, String word) {
        var content = getFileContent(path);
        var sentences = splitContent(content);
        var filteredSentences = filterSentencesContainWord(sentences, word);
        var countOfWord = getCountOfWord(filteredSentences, word);

        return new FileInformation(filteredSentences, countOfWord, word);
    }

    public void printResult(FileInformation fileInformation) {
        System.out.println("Word " + fileInformation.getWord() + " exist " + fileInformation.getWordCount() +
                " times. Sentences that exist: \n");
        fileInformation.getSentences().forEach(System.out::println);
    }

    protected String getFileContent(String path) {
        try (var fileInputStream = new FileInputStream(path)) {
            return getFileContent(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Can't read content from file by path: " + path, e);
        }
    }

    protected String getFileContent(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Can't read content from inputStream.", e);
        }
    }

    protected List<String> splitContent(String content) {
        return Arrays.stream(content.split("\r\n")).collect(Collectors.toList());
    }

    protected List<String> filterSentencesContainWord(List<String> sentences, String word) {
        return sentences.stream().filter(sentence -> sentence.toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    protected long getCountOfWord(List<String> filteredSentences, String word) {
        return filteredSentences.stream()
                .flatMap(filteredSentence -> Stream.of(filteredSentence.split(" ")))
                .map(String::toLowerCase)
                .filter(filteredWord -> filteredWord.contains(word.toLowerCase())).count();
    }
}
