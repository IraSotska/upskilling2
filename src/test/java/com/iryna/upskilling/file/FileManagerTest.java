package com.iryna.upskilling.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    private static final String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();

    @BeforeEach
    void prepareFiles() throws IOException {

        new File(FILE_SEPARATOR + "dir1").mkdir();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_1").mkdir();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2").mkdir();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3").mkdir();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "dir2_1").mkdir();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2").mkdir();

        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "file1.txt").createNewFile();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "file2.txt").createNewFile();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2" + FILE_SEPARATOR + "file3.txt").createNewFile();

        createFileWithContent(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "file1.txt", "Some content1");
        createFileWithContent(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "file2.txt", "Some content2");
        createFileWithContent(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2" + FILE_SEPARATOR + "file3.txt", "Some content3");
    }

    @AfterEach
    void clearFiles() {

        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "file1.txt").delete();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "file2.txt").delete();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2" + FILE_SEPARATOR + "file3.txt").delete();

        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "dir2_1").delete();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2").delete();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_1").delete();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_2").delete();
        new File(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3").delete();
        new File(FILE_SEPARATOR + "dir1").delete();
    }

    @Test
    @DisplayName("Should Count Files If Exist")
    void shouldCountFilesIfExist() {
        assertEquals(3, FileManager.countFiles(FILE_SEPARATOR + "dir1"));
    }

    @Test
    @DisplayName("Should Count Files If Not Exist")
    void shouldCountFilesIfNotExist() {
        assertEquals(0, FileManager.countFiles(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_1"));
    }

    @Test
    @DisplayName("Should Count Files If Exist One File")
    void shouldCountFilesIfExistOneFile() {
        assertEquals(1, FileManager.countFiles(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2"));
    }

    @Test
    @DisplayName("Should Count Directories If Exist")
    void shouldCountDirectoriesIfExist() {
        assertEquals(5, FileManager.countDirs(FILE_SEPARATOR + "dir1"));
    }

    @Test
    @DisplayName("Should Count Directories If Not Exist")
    void shouldCountDirectoriesIfNotExist() {
        assertEquals(0, FileManager.countFiles(FILE_SEPARATOR + "dir1" + FILE_SEPARATOR + "dir1_1"));
    }

    @Test
    @DisplayName("Should Copy Files If Exist")
    void shouldCopyFilesIfExist() {

        FileManager.copy(FILE_SEPARATOR + "dir1", FILE_SEPARATOR + "dir2");
        checkIfFilesExist(FILE_SEPARATOR + "dir1");
        compareFileContent(FILE_SEPARATOR + "dir1");
        checkIfFilesExist(FILE_SEPARATOR + "dir2"+ FILE_SEPARATOR + "dir1");
        compareFileContent(FILE_SEPARATOR + "dir2"+ FILE_SEPARATOR + "dir1");
    }

    @Test
    @DisplayName("Should Move Files")
    void shouldMoveFiles() {
        FileManager.move(FILE_SEPARATOR + "dir1", FILE_SEPARATOR + "dir2");

        assertFalse(new File( FILE_SEPARATOR + "dir1").exists());
        checkIfFilesExist(FILE_SEPARATOR + "dir2" + FILE_SEPARATOR + "dir1");
        compareFileContent(FILE_SEPARATOR + "dir2" + FILE_SEPARATOR + "dir1");
    }

    private void createFileWithContent(String path, String content) {
        try (var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path))) {
            bufferedOutputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIfFilesExist(String baseDirectory) {
        assertTrue(new File(baseDirectory + FILE_SEPARATOR + "file1.txt").exists());
        assertTrue(new File(baseDirectory + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "file2.txt").exists());
        assertTrue(new File(baseDirectory + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2" + FILE_SEPARATOR + "file3.txt").exists());
        assertTrue(new File(baseDirectory + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "dir2_1").exists());
        assertTrue(new File(baseDirectory + FILE_SEPARATOR + "dir1_1").exists());
        assertTrue(new File(baseDirectory + FILE_SEPARATOR + "dir1_3").exists());
    }

    private void compareFileContent(String baseDirectory) {
        assertEquals("Some content1", getFileContent(baseDirectory + FILE_SEPARATOR + "file1.txt"));
        assertEquals("Some content2", getFileContent(baseDirectory + FILE_SEPARATOR + "dir1_2" + FILE_SEPARATOR + "file2.txt"));
        assertEquals("Some content3", getFileContent(baseDirectory + FILE_SEPARATOR + "dir1_3" + FILE_SEPARATOR + "dir2_2" + FILE_SEPARATOR + "file3.txt"));
    }

    private String getFileContent(String path) {
        var result = new StringBuilder();
        try (var bufferedInputStream = new BufferedInputStream(new FileInputStream(path))) {
            var content = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(content)) != -1) {
                result.append(new String(content, 0, bytesRead));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}