package com.iryna.upskilling.file;

public class FileAnalyzer {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid arguments.");
        }

        var fileAnalyzerService = new FileAnalyzerService();
        var result = fileAnalyzerService.analyze(args[0], args[1]);
        fileAnalyzerService.printResult(result);
    }
}
