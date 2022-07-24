package com.iryna.upskilling.orm;

import com.iryna.upskilling.orm.annotation.Column;
import com.iryna.upskilling.orm.annotation.Id;
import com.iryna.upskilling.orm.annotation.Table;

@Table(name = "test_class")
public class TestClassWithoutGetter {

    @Id
    private Long id;

    @Column
    private String name;
}
