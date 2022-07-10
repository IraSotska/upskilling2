package com.iryna.upskilling.file;

import java.util.List;

public class FileInformation {

    private List<String> sentences;
    private String word;
    private long wordCount;

    public FileInformation(List<String> sentences, long wordCount, String word) {
        this.word = word;
        this.sentences = sentences;
        this.wordCount = wordCount;
    }

    public String getWord() {
        return word;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public long getWordCount() {
        return wordCount;
    }
}
