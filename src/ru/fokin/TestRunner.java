package ru.fokin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestRunner {
    public static void runTests(Class<?> clazz) throws Exception {
        checkAnnotations(clazz);

        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Выполняем BeforeSuite
        runAnnotatedStaticMethods(clazz, BeforeSuite.class, null);

        // Собираем и сортируем тесты
        List<Method> testMethods = getTestMethods(clazz);

        // Выполняем тесты
        for (Method testMethod : testMethods) {
            runBeforeAfterTest(instance, BeforeTest.class);
            runTestMethod(instance, testMethod);
            runBeforeAfterTest(instance, AfterTest.class);
        }

        // Выполняем AfterSuite
        runAnnotatedStaticMethods(clazz, AfterSuite.class, null);
    }

    private static void checkAnnotations(Class<?> clazz) {
        checkSingleAnnotation(clazz, BeforeSuite.class);
        checkSingleAnnotation(clazz, AfterSuite.class);
    }

    private static void checkSingleAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annotation))
                .toList();

        if (methods.size() > 1) {
            throw new RuntimeException("Multiple " + annotation.getSimpleName() + " methods");
        }

        methods.forEach(m -> {
            if (!Modifier.isStatic(m.getModifiers())) {
                throw new RuntimeException(annotation.getSimpleName() + " method must be static");
            }
        });
    }

    private static List<Method> getTestMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparingInt((Method m) ->
                        m.getAnnotation(Test.class).priority()).reversed())
                .toList();
    }

    private static void runAnnotatedStaticMethods(Class<?> clazz,
                                                  Class<? extends Annotation> annotation, Object instance) throws Exception {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation) && Modifier.isStatic(method.getModifiers())) {
                method.invoke(instance);
            }
        }
    }

    private static void runBeforeAfterTest(Object instance,
                                           Class<? extends Annotation> annotation) throws Exception {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation) &&
                    !Modifier.isStatic(method.getModifiers())) {
                method.invoke(instance);
            }
        }
    }

    private static void runTestMethod(Object instance, Method method) throws Exception {
        CsvSource csvSource = method.getAnnotation(CsvSource.class);
        if (csvSource != null) {
            String[] args = csvSource.value().split(",\\s*");
            invokeWithArgs(instance, method, args);
        } else {
            if (method.getParameterCount() != 0) {
                throw new RuntimeException("Method requires parameters but no CsvSource provided");
            }
            method.invoke(instance);
        }
    }

    private static void invokeWithArgs(Object instance, Method method, String[] args) throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (args.length != paramTypes.length) {
            throw new RuntimeException("Argument count mismatch");
        }

        Object[] convertedArgs = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            convertedArgs[i] = convertValue(args[i], paramTypes[i]);
        }

        method.invoke(instance, convertedArgs);
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == String.class) {
            return value;
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + targetType);
        }
    }
    public static void main(String[] args) throws Exception {
        TestRunner.runTests(TestClass.class);
    }
}
