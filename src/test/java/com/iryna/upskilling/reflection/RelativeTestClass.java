package com.iryna.upskilling.reflection;

public class RelativeTestClass implements Cloneable {

    @Override
    public RelativeTestClass clone() {
        try {
            return (RelativeTestClass) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
