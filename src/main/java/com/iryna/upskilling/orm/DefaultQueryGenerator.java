package com.iryna.upskilling.orm;

import com.iryna.upskilling.orm.annotation.Column;
import com.iryna.upskilling.orm.annotation.Id;
import com.iryna.upskilling.orm.annotation.Table;

import java.io.Serializable;
import java.lang.reflect.Field;
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
            throw new IllegalArgumentException("ORM class must have table annotation.");
        }

        var idFieldListSize = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null).collect(Collectors.toList()).size();

        if (idFieldListSize > 1) {
            throw new IllegalArgumentException("Class must have one id. Current count of id: " + idFieldListSize);
        }

        if (idFieldListSize < 1) {
            throw new IllegalArgumentException("ORM class must have id annotation.");
        }
    }

    private List<String> getColumnNamesList(Class<?> clazz) {
        return getColumnFieldsList(clazz).stream()
                .map(field -> field.getAnnotation(Column.class).name().isEmpty() ?
                        field.getName() :
                        field.getAnnotation(Column.class).name())
                .collect(Collectors.toList());
    }

    private List<String> getValuesOfFieldsList(Object object) {
        return getColumnFieldsList(object.getClass()).stream()
                .map(field -> addQuotesIfStringValue(field.getType(), getFieldValue(object, field)))
                .collect(Collectors.toList());
    }

    private List<Field> getColumnFieldsList(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotation(Column.class) != null).collect(Collectors.toList());
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
        return getFieldValue(object, Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null).collect(Collectors.toList()).get(0));
    }

    private String getFieldValue(Object object, Field field) {
        try {
            var fieldName = field.getName();
            var getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            return object.getClass().getDeclaredMethod(getterName).invoke(object).toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't get field value for field: " + field.getName(), e);
        }
    }
}
