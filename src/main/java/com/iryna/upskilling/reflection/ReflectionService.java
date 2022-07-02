package com.iryna.upskilling.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionService {

    public static <T> Object createObjectByClass(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    public static void executeAllMethodsWithoutParametersAtObject(Object object) {
        Arrays.stream(object.getClass().getDeclaredMethods()).filter(method -> method.getParameters().length == 0)
                .forEach(method -> {
                    method.setAccessible(true);
                    try {
                        method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static void printSignaturesOfFinalMethodsAtObject(Object object) {
        getFinalMethodsAtObject(object).forEach(method -> {
            System.out.println("Method: " + method.getName() + "ReturnType: " + method.getGenericReturnType());

            Arrays.stream(method.getParameters()).forEach(parameter -> System.out.println("Parameter name: " +
                    parameter.getName() + " type: " + parameter.getType()));
        });
    }

    public static <T> void printNotPublicMethodsOfClass(Class<T> clazz) {
        getNotPublicMethodsOfClass(clazz).stream()
                .map(Method::getName)
                .forEach(name -> System.out.println("Method name: " + name));
    }

    public static <T> void printAncestriesOfClass(Class<T> clazz) {
        getAllInterfacesAndAncestriesOfClass(clazz).forEach(ancestor -> System.out.println("Ancestor: " + ancestor));
    }

    public static void changePrivateFieldsToDefaultAtObject(Object object) {
        Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> {
                    var fieldType = field.getType();
                    try {
                        if ((fieldType == int.class) || (fieldType == byte.class) || (fieldType == long.class)) {
                            field.set(object, 0);
                        } else if ((fieldType == double.class) || fieldType == float.class) {
                            field.set(object, 0.0);
                        } else if (fieldType == boolean.class) {
                            field.set(object, false);
                        } else if (fieldType == char.class) {
                            field.set(object, '\u0000');
                        } else if (fieldType == short.class) {
                            field.set(object, (short) 0);
                        } else {
                            field.set(object, null);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    public static Set<Method> getFinalMethodsAtObject(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method -> Modifier.isFinal(method.getModifiers()))
                .collect(Collectors.toSet());
    }

    public static <T> Set<Method> getNotPublicMethodsOfClass(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> !Modifier.isPublic(method.getModifiers()))
                .collect(Collectors.toSet());
    }

    public static <T> Set<Class> getAllInterfacesAndAncestriesOfClass(Class<T> clazz) {
        Set<Class> superClasses = getAncestriesOfClass(clazz.getSuperclass(), new HashSet<>());

        Set<Class> result = new HashSet<>();
        for (Class ancestor : superClasses) {
            result.addAll(getInterfacesOfClass(ancestor));
        }
        result.addAll(getInterfacesOfClass(clazz));
        result.addAll(superClasses);

        return result;
    }

    private static Set<Class> getInterfacesOfClass(Class clazz) {
        Set<Class> res = new HashSet<>();
        for (Class currentInterface : clazz.getInterfaces()) {
            res.addAll(getAncestriesOfClass(currentInterface, new HashSet<>()));
        }
        return res;
    }

    private static <T> Set<Class> getAncestriesOfClass(Class<T> clazz, Set<Class> result) {
        result.add(clazz);
        var superClass = clazz.getSuperclass();
        if (superClass == null) {
            return result;
        }
        return getAncestriesOfClass(superClass, result);
    }
}
