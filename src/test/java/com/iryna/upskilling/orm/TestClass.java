package com.iryna.upskilling.orm;

import com.iryna.upskilling.orm.annotation.Column;
import com.iryna.upskilling.orm.annotation.Id;
import com.iryna.upskilling.orm.annotation.Table;

@Table(name = "test_class")
public class TestClass {

    public TestClass(Long id, String name, String testField) {
        this.id = id;
        this.name = name;
        this.testField = testField;
    }

    public TestClass(Long idToUpdate, Long id, String name, String testField) {
        this.idToUpdate = idToUpdate;
        this.id = id;
        this.name = name;
        this.testField = testField;
    }

    @Id
    private Long idToUpdate;

    @Column
    private Long id;

    @Column
    private String name;

    @Column(name = "test_field")
    private String testField;

    public Long getIdToUpdate() {
        return idToUpdate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTestField() {
        return testField;
    }
}
