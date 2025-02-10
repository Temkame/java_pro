package ru.fokin;

public class Employee {
    String name;
    int age;
    String position;

    Employee(String name, int age, String position) {
        this.name = name;
        this.age = age;
        this.position = position;
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    String getPosition() {
        return position;
    }
}
