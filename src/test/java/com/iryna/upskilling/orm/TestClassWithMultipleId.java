package com.iryna.upskilling.orm;

import com.iryna.upskilling.orm.annotation.Id;
import com.iryna.upskilling.orm.annotation.Table;

@Table(name = "test_class")
public class TestClassWithMultipleId {

    @Id
    private String id;

    @Id
    private String id2;

    @Id
    private String id3;
}
