package com.iryna.upskilling.orm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultQueryGeneratorTest {

    private final QueryGenerator defaultQueryGenerator = new DefaultQueryGenerator();
    private static final List<String> FIELDS_NAMES = List.of("name", "id", "test_field");
    private static final Map<String, String> FIELDS_NAMES_TO_VALUES = Map.of("name", "'name'", "id", "1",
            "test_field", "'test'");
    private static final String TABLE_NAME = "test_class";

    @DisplayName("Should Create Query Get All Test")
    @Test
    public void shouldCreateQueryGetAllTest() {
        var resultQuery = defaultQueryGenerator.findAll(TestClass.class);

        for (var field : FIELDS_NAMES) {
            assertTrue(resultQuery.contains(field));
        }
        assertTrue(resultQuery.startsWith("SELECT"));
        assertTrue(resultQuery.endsWith("FROM " + TABLE_NAME + ";"));
    }

    @DisplayName("Should Throw Exception If Create Query Get All Without Table Annotation Test")
    @Test
    public void shouldThrowExceptionIfCreateQueryGetAllWithoutTableAnnotationTest() {
        assertThatThrownBy(() -> defaultQueryGenerator.findAll(DefaultQueryGeneratorTest.class))
                .hasMessage("Class is not ORM entity.")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Should Create Query Find By Id Test")
    @Test
    public void shouldCreateQueryFindByIdTest() {
        var expectedQuery = "SELECT * FROM test_class WHERE id = 1;";
        var resultQuery = defaultQueryGenerator.findById(TestClass.class, 1L);

        assertEquals(expectedQuery, resultQuery);
    }

    @DisplayName("Should Throw Exception If Create Query Find By Id Without Table Annotation Test")
    @Test
    public void shouldThrowExceptionIfCreateQueryFindByIdWithoutTableAnnotationTest() {
        assertThatThrownBy(() -> defaultQueryGenerator.findById(DefaultQueryGeneratorTest.class, 1L))
                .hasMessage("Class is not ORM entity.")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Should Create Query Delete By Id Test")
    @Test
    public void shouldCreateQueryDeleteByIdTest() {
        var expectedQuery = "DELETE * FROM test_class WHERE id = 1;";
        var resultQuery = defaultQueryGenerator.deleteById(TestClass.class, 1L);

        assertEquals(expectedQuery, resultQuery);
    }

    @DisplayName("Should Throw Exception If Create Query Delete By Id Without Table Annotation Test")
    @Test
    public void shouldThrowExceptionIfCreateQueryDeleteByIdWithoutTableAnnotationTest() {
        assertThatThrownBy(() -> defaultQueryGenerator.deleteById(DefaultQueryGeneratorTest.class, 1L))
                .hasMessage("Class is not ORM entity.")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Should Create Insert Query Test")
    @Test
    public void shouldCreateInsertQueryTest() {
        var expectedStart = "INSERT INTO " + TABLE_NAME + " (";
        var resultQuery = defaultQueryGenerator.insert(new TestClass(1L, "name", "test"));
        var fieldsNames = resultQuery.substring(expectedStart.length(), resultQuery.indexOf(')'));
        var namesOfFieldsAsList = Arrays.asList(fieldsNames.split(", "));
        var valuesOfFieldsAsList = Arrays.asList(resultQuery.substring(resultQuery.lastIndexOf("(") + 1,
                resultQuery.lastIndexOf(")")).split(", "));

        for (int i = 0; i < namesOfFieldsAsList.size(); i++) {
            assertEquals(valuesOfFieldsAsList.get(i), FIELDS_NAMES_TO_VALUES.get(namesOfFieldsAsList.get(i)));
        }

        assertEquals(") VALUES (", resultQuery.substring(expectedStart.length() + fieldsNames.length(),
                resultQuery.lastIndexOf('(') + 1));
        assertTrue(resultQuery.startsWith(expectedStart));
        assertTrue(resultQuery.endsWith(");"));
    }

    @DisplayName("Should Throw Exception If Create Insert Query Without Table Annotation Test")
    @Test
    public void shouldThrowExceptionIfCreateInsertQueryWithoutTableAnnotationTest() {
        assertThatThrownBy(() -> defaultQueryGenerator.insert(new DefaultQueryGeneratorTest()))
                .hasMessage("Class is not ORM entity.")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Should Create Update Query Test")
    @Test
    public void shouldCreateUpdateQueryTest() {
        var resultQuery = defaultQueryGenerator.update(new TestClass(1L, 1L, "name", "test"));
        var expectedStart = "UPDATE " + TABLE_NAME + " SET ";
        var expectedEnd = " WHERE id = 1;";

        var namesOfFieldsAsList = new HashSet<>(Arrays.asList(resultQuery.substring(expectedStart.length(),
                resultQuery.indexOf(expectedEnd)).split(", ")));
        var fieldNameToValue = FIELDS_NAMES_TO_VALUES.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue()).collect(Collectors.toSet());

        assertEquals(fieldNameToValue, namesOfFieldsAsList);
        assertTrue(resultQuery.startsWith(expectedStart));
        assertTrue(resultQuery.endsWith(expectedEnd));
    }

    @DisplayName("Should Throw Exception If Update Insert Query Without Table Annotation Test")
    @Test
    public void shouldThrowExceptionIfCreateUpdateQueryWithoutTableAnnotationTest() {
        assertThatThrownBy(() -> defaultQueryGenerator.update(DefaultQueryGeneratorTest.class))
                .hasMessage("Class is not ORM entity.")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Should Throw Exception If Update Insert Query Without Id Annotation Test")
    @Test
    public void shouldThrowExceptionIfCreateUpdateQueryWithoutIdAnnotationTest() {
        assertThatThrownBy(() -> defaultQueryGenerator.update(new TestClassWithoutId()))
                .hasMessage("Class don't have id.")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
