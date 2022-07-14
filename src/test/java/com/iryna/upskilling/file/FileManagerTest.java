package com.iryna.upskilling.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    @BeforeEach
    void prepareFiles() throws IOException {

        new File("/dir1").mkdir();
        new File("/dir1/dir1_1").mkdir();
        new File("/dir1/dir1_2").mkdir();
        new File("/dir1/dir1_3").mkdir();
        new File("/dir1/dir1_2/dir2_1").mkdir();
        new File("/dir1/dir1_3/dir2_2").mkdir();

        new File("/dir1/file1.txt").createNewFile();
        new File("/dir1/dir1_2/file2.txt").createNewFile();
        new File("/dir1/dir1_3/dir2_2/file3.txt").createNewFile();

        createFileWithContent("/dir1/file1.txt", "Some content1");
        createFileWithContent("/dir1/dir1_2/file2.txt", "Some content2");
        createFileWithContent("/dir1/dir1_3/dir2_2/file3.txt", "Some content3");
    }

    @AfterEach
    void clearFiles() {

        new File("/dir1/file1.txt").delete();
        new File("/dir1/dir1_2/file2.txt").delete();
        new File("/dir1/dir1_3/dir2_2/file3.txt").delete();

        new File("/dir1/dir1_2/dir2_1").delete();
        new File("/dir1/dir1_3/dir2_2").delete();
        new File("/dir1/dir1_1").delete();
        new File("/dir1/dir1_2").delete();
        new File("/dir1/dir1_3").delete();
        new File("/dir1").delete();
    }

    @Test
    @DisplayName("Should Count Files If Exist")
    void shouldCountFilesIfExist() {
        assertEquals(3, FileManager.countFiles("/dir1"));
    }

    @Test
    @DisplayName("Should Count Files If Not Exist")
    void shouldCountFilesIfNotExist() {
        assertEquals(0, FileManager.countFiles("/dir1/dir1_1"));
    }

    @Test
    @DisplayName("Should Count Files If Exist One File")
    void shouldCountFilesIfExistOneFile() {
        assertEquals(1, FileManager.countFiles("/dir1/dir1_3/dir2_2"));
    }

    @Test
    @DisplayName("Should Count Directories If Exist")
    void shouldCountDirectoriesIfExist() {
        assertEquals(5, FileManager.countDirs("/dir1"));
    }

    @Test
    @DisplayName("Should Count Directories If Not Exist")
    void shouldCountDirectoriesIfNotExist() {
        assertEquals(0, FileManager.countFiles("/dir1/dir1_1"));
    }

    @Test
    @DisplayName("Should Copy Files If Exist")
    void shouldCopyFilesIfExist() throws IOException {

        FileManager.copy("/dir1", "/dir2");
        checkIfFilesExist("/dir1");
        compareFileContent("/dir1");
        checkIfFilesExist("/dir2/dir1");
        compareFileContent("/dir2/dir1");
    }

    @Test
    @DisplayName("Should Move Files")
    void shouldMoveFiles() throws IOException {
        FileManager.move("/dir1", "/dir2");

        assertFalse(new File("/dir1").exists());
        checkIfFilesExist("/dir2/dir1");
        compareFileContent("/dir2/dir1");
    }

    private void createFileWithContent(String path, String content) {
        try (var bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path))) {
            bufferedOutputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIfFilesExist(String baseDirectory) {
        assertTrue(new File(baseDirectory, "/file1.txt").exists());
        assertTrue(new File(baseDirectory, "/dir1_2/file2.txt").exists());
        assertTrue(new File(baseDirectory, "/dir1_3/dir2_2/file3.txt").exists());
        assertTrue(new File(baseDirectory, "/dir1_2/dir2_1").exists());
        assertTrue(new File(baseDirectory, "/dir1_1").exists());
        assertTrue(new File(baseDirectory, "/dir1_3").exists());
    }

    private void compareFileContent(String baseDirectory) throws IOException {
        assertEquals("Some content1", getFileContent(baseDirectory + "/file1.txt"));
        assertEquals("Some content2", getFileContent(baseDirectory + "/dir1_2/file2.txt"));
        assertEquals("Some content3", getFileContent(baseDirectory + "/dir1_3/dir2_2/file3.txt"));
    }

    private String getFileContent(String path) throws IOException {
        var result = new StringBuilder();
        var bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
        var content = new byte[1024];
        int bytesRead;
        while ((bytesRead = bufferedInputStream.read(content)) != -1) {
            result.append(new String(content, 0, bytesRead));
        }

        return result.toString();
    }
}