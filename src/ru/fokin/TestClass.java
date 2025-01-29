package ru.fokin;

public class TestClass {
    @BeforeSuite
    public static void beforeSuite() {
        System.out.println("BeforeSuite");
    }

    @Test(priority = 1)
    @CsvSource("5, Hello, 10, true")
    public void test1(int a, String b, int c, boolean d) {
        System.out.printf("Test1: a=%d, b=%s, c=%d, d=%b%n", a, b, c, d);
    }

    @Test(priority = 8)
    public void test2() {
        System.out.println("Test2");
    }

    @AfterSuite
    public static void afterSuite() {
        System.out.println("AfterSuite");
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("BeforeTest");
    }

    @AfterTest
    public void afterTest() {
        System.out.println("AfterTest");
    }
}
