package com.iryna.upskilling.orm;

import com.iryna.upskilling.orm.annotation.Column;
import com.iryna.upskilling.orm.annotation.Id;
import com.iryna.upskilling.orm.annotation.Table;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultQueryGenerator implements QueryGenerator {

    @Override
    public String findAll(Class<?> type) {
        checkIfORMClass(type);

        var queryBuilder = new StringBuilder("SELECT ");
        queryBuilder.append(createStringViaComma(getColumnNamesList(type)));
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
        queryBuilder.append(addQuotesIfStringValue(id.getClass(), id.toString()));
        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    @Override
    public String deleteById(Class<?> type, Serializable id) {
        checkIfORMClass(type);

        var queryBuilder = new StringBuilder("DELETE * FROM ");
        queryBuilder.append(getTableName(type));
        queryBuilder.append(" WHERE id = ");
        queryBuilder.append(addQuotesIfStringValue(id.getClass(), id.toString()));
        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    @Override
    public String insert(Object object) {
        checkIfORMClass(object.getClass());

        var queryBuilder = new StringBuilder("INSERT INTO ");
        queryBuilder.append(getTableName(object.getClass()));
        queryBuilder.append(" (");
        queryBuilder.append(createStringViaComma(getColumnNamesList(object.getClass())));
        queryBuilder.append(") VALUES (");
        queryBuilder.append(createStringViaComma(getValuesOfFieldsList(object)));
        queryBuilder.append(");");

        return queryBuilder.toString();
    }

    @Override
    public String update(Object object) {
        checkIfORMClass(object.getClass());

        var stringJoiner = new StringJoiner(", ");
        var fieldsNames = getColumnNamesList(object.getClass());
        var fieldsValues = getValuesOfFieldsList(object);

        for (int i = 0; i < fieldsNames.size(); i++) {
            stringJoiner.add(fieldsNames.get(i) + " = " + fieldsValues.get(i));
        }

        var queryBuilder = new StringBuilder("UPDATE ");
        queryBuilder.append(getTableName(object.getClass()));
        queryBuilder.append(" SET ");
        queryBuilder.append(stringJoiner);
        queryBuilder.append(" WHERE id = ");
        queryBuilder.append(addQuotesIfStringValue(object.getClass(), getIdFromObject(object)));
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
        for (var field : clazz.getDeclaredFields()) {
            var columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                result.add(Objects.equals(columnAnnotation.name(), "") ? field.getName() : columnAnnotation.name());
            }
        }
        return result;
    }

    private List<String> getValuesOfFieldsList(Object object) {
        var result = new ArrayList<String>();

        for (var field : object.getClass().getDeclaredFields()) {
            var columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                var fieldValue = field.get(object).toString();
                fieldValue = addQuotesIfStringValue(field.getType(), fieldValue);
                result.add(fieldValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Don't have access to field: " + field.getName(), e);
            }
        }
        return result;
    }

    private String addQuotesIfStringValue(Class<?> type, String value) {
        return CharSequence.class.isAssignableFrom(type) ? "'" + value + "'" : value;
    }

    private String createStringViaComma(List<String> values) {
        var stringJoiner = new StringJoiner(", ");
        values.forEach(stringJoiner::add);
        return stringJoiner.toString();
    }

    private String getTableName(Class<?> clazz) {
        var tableAnnotation = clazz.getAnnotation(Table.class);
        return tableAnnotation == null ? clazz.getSimpleName() : tableAnnotation.name();
    }

    private String getIdFromObject(Object object) {
        var idFieldList = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null).collect(Collectors.toList());

        if (idFieldList.size() != 1) {
            throw new IllegalArgumentException("Class must have one id. Current count of id: " + idFieldList.size());
        }

        var idField = idFieldList.get(0);
        idField.setAccessible(true);
        try {
            return idField.get(object).toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Don't have access to field: " + idFieldList.get((Integer) object).getName(), e);
        }
    }
}
