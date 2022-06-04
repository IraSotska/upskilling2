package com.iryna.upskilling.reflection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionServiceTest {

    @DisplayName("Create Object By Class")
    @Test
    void createObjectByClass() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        assertThat(ReflectionService.createObjectByClass(TestClass.class)).isNotNull().isInstanceOf(TestClass.class);
    }

    @DisplayName("Execute All Methods Without Parameters At Object")
    @Test
    void executeAllMethodsWithoutParametersAtObject() {
        var object = new TestClass();
        ReflectionService.executeAllMethodsWithoutParametersAtObject(object);

        assertThat(object.getIsMethodWithoutParametersInvoked()).isTrue();
    }

    @DisplayName("Get Signatures Of Final Methods At Object")
    @Test
    void getSignaturesOfFinalMethodsAtObject() {
        var object = new TestClass();
        var resultMethodsNames = ReflectionService.getFinalMethodsAtObject(object).stream()
                .map(Method::getName).collect(Collectors.toSet());

        assertThat(resultMethodsNames).isEqualTo(Set.of("finalMethod", "finalSynchronizedMethod", "finalStaticMethod"));
    }

    @DisplayName("Get Not Public Methods Of Class")
    @Test
    void getNotPublicMethodsOfClass() {
        var expectedResult = Set.of("finalMethod", "protectedMethod", "methodWithoutParameters", "privateMethod");
        var result = ReflectionService.getNotPublicMethodsOfClass(TestClass.class).stream()
                .map(Method::getName)
                .collect(Collectors.toSet());

        assertThat(result).isEqualTo(expectedResult);
    }

    @DisplayName("Get Ancestries Of Class")
    @Test
    void getAncestriesOfClass() {
        var expectedResult = Set.of(RelativeTestClass.class, Cloneable.class, Object.class);

        assertThat(ReflectionService.getAllInterfacesAndAncestriesOfClass(TestClass.class)).isEqualTo(expectedResult);
    }

    @DisplayName("Change Private Fields To Default At Object")
    @Test
    void changePrivateFieldsToDefaultAtObject() {
        var object = new TestClass();
        ReflectionService.changePrivateFieldsToDefaultAtObject(object);

        assertThat(object.getSomeChar()).isEqualTo('\u0000');
        assertThat(object.getSomeDouble()).isEqualTo(0.0D);
        assertThat(object.getSomeLong()).isEqualTo(0L);
        assertThat(object.getSomeInt()).isEqualTo(0);
        assertThat(object.isSomeBoolean()).isEqualTo(false);
        assertThat(object.getSomeString()).isEqualTo(null);
        assertThat(object.getSomeShort()).isEqualTo((short) 0);
    }
}