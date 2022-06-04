package com.iryna.upskilling.reflection;

public class TestClass extends RelativeTestClass {
    private boolean isMethodWithoutParametersInvoked;
    private String someString = "some";
    private int someInt = 1;
    private double someDouble = 1.1D;
    private long someLong = 11;
    private char someChar = '1';
    private short someShort = 1;
    private boolean someBoolean = true;

    void methodWithoutParameters() {
        isMethodWithoutParametersInvoked = true;
    }

    final void finalMethod(int i) {
    }

    public final static void finalStaticMethod(boolean b, String s) {
    }

    public final synchronized void finalSynchronizedMethod(int[] arr) {
    }

    private void privateMethod() {
    }

    protected void protectedMethod() {
    }

    public boolean getIsMethodWithoutParametersInvoked() {
        return isMethodWithoutParametersInvoked;
    }

    public String getSomeString() {
        return someString;
    }

    public int getSomeInt() {
        return someInt;
    }

    public double getSomeDouble() {
        return someDouble;
    }

    public long getSomeLong() {
        return someLong;
    }

    public char getSomeChar() {
        return someChar;
    }

    public short getSomeShort() {
        return someShort;
    }

    public boolean isSomeBoolean() {
        return someBoolean;
    }
}
