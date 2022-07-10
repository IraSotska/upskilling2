package com.iryna.upskilling.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    public static int countFiles(String path) {
        return countFilesByPath(path, 0);
    }

    public static int countDirs(String path) {
        return countDirectoriesByPath(path, 0);
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

    private static void createNewFileByPath(String path) {
        try {
            new File(path).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Can't create new file at path: " + path, e);
        }
    }

    private static void copyFileContent(String pathFromCopy, String pathToCopy) {
        createNewFileByPath(pathToCopy + pathFromCopy);

        try (var fileInputStream = new FileInputStream(pathFromCopy);
             var fileOutputStream = new FileOutputStream(pathToCopy + pathFromCopy)) {
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

    private static int countDirectoriesByPath(String path, int countFiles) {
        var files = new File(path).listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isDirectory()) {
                    countFiles++;
                    countFiles += countFiles(file.getPath());
                }
            }
        }
        return countFiles;
    }

    private static int countFilesByPath(String path, int countFiles) {
        var files = new File(path).listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isFile()) {
                    countFiles++;
                }
                if (file.isDirectory()) {
                    countFiles += countFiles(file.getPath());
                }
            }
        }
        return countFiles;
    }
}
