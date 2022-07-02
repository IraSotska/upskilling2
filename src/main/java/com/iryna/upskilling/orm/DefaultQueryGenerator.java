package com.iryna.upskilling.orm;

import com.iryna.upskilling.orm.annotation.Column;
import com.iryna.upskilling.orm.annotation.Id;
import com.iryna.upskilling.orm.annotation.Table;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class DefaultQueryGenerator implements QueryGenerator {

    @Override
    public String findAll(Class<?> type) {
        checkIfORMClass(type);

        var queryBuilder = new StringBuilder("SELECT ");
        queryBuilder.append(createValuesStringViaComma(getColumnNamesList(type)));
        queryBuilder.append(" FROM ");
        queryBuilder.append(getTableName(type));
        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    @Override
    public String findById(Class<?> type, Serializable id) {
        checkIfORMClass(type);

        var queryBuilder = new StringBuilder("SELECT * FROM ");
        queryBuilder.append(getTableName(type));
        queryBuilder.append(" WHERE id = ");
        queryBuilder.append(id);
        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        checkIfORMClass(type);

        var queryBuilder = new StringBuilder("DELETE * FROM ");
        queryBuilder.append(getTableName(type));
        queryBuilder.append(" WHERE id = ");
        queryBuilder.append(id);
        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    @Override
    public String insert(Object value) {
        checkIfORMClass(value.getClass());

        var queryBuilder = new StringBuilder("INSERT INTO ");
        queryBuilder.append(getTableName(value.getClass()));
        queryBuilder.append(" (");
        queryBuilder.append(createValuesStringViaComma(getColumnNamesList(value.getClass())));
        queryBuilder.append(") VALUES (");
        queryBuilder.append(createValuesStringViaComma(getFieldsValuesList(value)));
        queryBuilder.append(");");

        return queryBuilder.toString();
    }

    @Override
    public String update(Object value) {
        checkIfORMClass(value.getClass());

        var stringJoiner = new StringJoiner(", ");
        var fieldsNames = getColumnNamesList(value.getClass());
        var fieldsValues = getFieldsValuesList(value);

        for (int i = 0; i < fieldsNames.size(); i++) {
            stringJoiner.add(fieldsNames.get(i) + " = " + fieldsValues.get(i));
        }

        var queryBuilder = new StringBuilder("UPDATE ");
        queryBuilder.append(getTableName(value.getClass()));
        queryBuilder.append(" SET ");
        queryBuilder.append(stringJoiner);
        queryBuilder.append(" WHERE id = ");
        queryBuilder.append(getObjectId(value));
        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    private void checkIfORMClass(Class<?> clazz) {
        if (clazz.getAnnotation(Table.class) == null) {
            throw new IllegalArgumentException("Class is not ORM entity.");
        }
    }

    private List<String> getColumnNamesList(Class<?> clazz) {
        var result = new ArrayList<String>();
        for (Field field : clazz.getDeclaredFields()) {
            var columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                result.add(Objects.equals(columnAnnotation.name(), "") ? field.getName() : columnAnnotation.name());
            }
        }
        return result;
    }

    private List<String> getFieldsValuesList(Object value) {
        var result = new ArrayList<String>();

        for (var field : value.getClass().getDeclaredFields()) {
            var columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                field.setAccessible(true);
                try {
                    var fieldValue = field.get(value).toString();
                    if (String.class.equals(field.getType()) || char.class.equals(field.getType())) {
                        fieldValue = "'" + fieldValue + "'";
                    }
                    result.add(fieldValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private String createValuesStringViaComma(List<String> values) {
        var stringJoiner = new StringJoiner(", ");
        for (String value : values) {
            stringJoiner.add(value);
        }
        return stringJoiner.toString();
    }

    private String getTableName(Class<?> clazz) {
        var tableAnnotation = clazz.getAnnotation(Table.class);
        return tableAnnotation == null ? clazz.getSimpleName() : tableAnnotation.name();
    }

    private String getObjectId(Object value) {
        var idField = Arrays.stream(value.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null).findFirst();

        if (idField.isPresent()) {
            idField.get().setAccessible(true);
            try {
                return idField.get().get(value).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalArgumentException("Class don't have id.");
    }
}
