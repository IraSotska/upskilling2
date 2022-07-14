package com.iryna.upskilling.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    public static int countFiles(String path) {
        return countFilesByPath(path);
    }

    public static int countDirs(String path) {
        return countDirectoriesByPath(path);
    }

    public static void move(String from, String to) {
        copy(from, to);
        removeFile(new File(from));
    }

    public static void copy(String from, String to) {
        new File(to).mkdir();
        new File(to + from).mkdir();
        copyDirectoriesAndFiles(from, to);
    }

    private static void copyDirectoriesAndFiles(String from, String to) {
        var files = new File(from).listFiles();

        if (files != null) {
            for (var file : files) {
                if (file.isFile()) {
                    copyFileContent(file.getPath(), to);
                }
                if (file.isDirectory()) {
                    new File(to + file.getPath()).mkdir();
                    copyDirectoriesAndFiles(file.getPath(), to);
                }
            }
        }
    }

    private static void copyFileContent(String pathFromCopy, String pathToCopy) {
        try (var fileInputStream = new FileInputStream(pathFromCopy);
             var fileOutputStream = new FileOutputStream(new File(pathToCopy, pathFromCopy))) {
            var buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't copy file content from file: " + pathFromCopy, e);
        }
    }

    private static void removeFile(File file) {
        var childFiles = file.listFiles();
        if (childFiles != null) {
            for (var childFile : childFiles) {
                removeFile(childFile);
            }
        }
        file.delete();
    }

    private static int countDirectoriesByPath(String path) {
        var directoriesCounter = 0;
        var files = new File(path).listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isDirectory()) {
                    directoriesCounter++;
                    directoriesCounter += countFiles(file.getPath());
                }
            }
        }
        return directoriesCounter;
    }

    private static int countFilesByPath(String path) {
        var filesCounter = 0;
        var files = new File(path).listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isFile()) {
                    filesCounter++;
                }
                if (file.isDirectory()) {
                    filesCounter += countFilesByPath(file.getPath());
                }
            }
        }
        return filesCounter;
    }
}
