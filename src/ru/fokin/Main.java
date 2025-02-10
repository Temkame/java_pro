package ru.fokin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Удаление дубликатов из списка
        List<Integer> listWithDuplicates = Arrays.asList(1, 2, 2, 3, 4, 4, 5);
        List<Integer> listWithoutDuplicates = listWithDuplicates.stream().distinct().collect(Collectors.toList());
        System.out.println("Список без дубликатов: " + listWithoutDuplicates);

        // 3-е наибольшее число
        List<Integer> numbers = Arrays.asList(5, 2, 10, 9, 4, 3, 10, 1, 13);
        int thirdLargest = numbers.stream().sorted(Comparator.reverseOrder()).distinct().skip(2).findFirst().orElse(-1);
        System.out.println("3-е наибольшее число: " + thirdLargest);

        // 3-е наибольшее уникальное число
        int thirdLargestUnique = numbers.stream().distinct().sorted(Comparator.reverseOrder()).skip(2).findFirst().orElse(-1);
        System.out.println("3-е наибольшее уникальное число: " + thirdLargestUnique);

        // Имена 3 самых старших инженеров
        List<Employee> employees = Arrays.asList(
                new Employee("Иван", 30, "Инженер"),
                new Employee("Петр", 25, "Инженер"),
                new Employee("Сергей", 35, "Менеджер"),
                new Employee("Анна", 40, "Инженер"),
                new Employee("Дмитрий", 45, "Инженер")
        );
        List<String> top3Engineers = employees.stream()
                .filter(e -> "Инженер".equals(e.getPosition()))
                .sorted(Comparator.comparingInt(Employee::getAge).reversed())
                .limit(3)
                .map(Employee::getName)
                .collect(Collectors.toList());
        System.out.println("Имена 3 самых старших инженеров: " + top3Engineers);

        // Средний возраст инженеров
        double averageAge = employees.stream()
                .filter(e -> "Инженер".equals(e.getPosition()))
                .mapToInt(Employee::getAge)
                .average()
                .orElse(0);
        System.out.println("Средний возраст инженеров: " + averageAge);

        // Самое длинное слово в списке
        List<String> words = Arrays.asList("яблоко", "груша", "банан", "апельсин");
        String longestWord = words.stream().max(Comparator.comparingInt(String::length)).orElse("");
        System.out.println("Самое длинное слово: " + longestWord);

        // Хеш-мапа с количеством вхождений слов
        String text = "яблоко груша банан апельсин банан груша";
        Map<String, Long> wordCountMap = Arrays.stream(text.split(" ")).collect(Collectors.groupingBy(w -> w, Collectors.counting()));
        System.out.println("Хеш-мапа: " + wordCountMap);

        // Сортировка строк по длине и алфавиту
        List<String> strings = Arrays.asList("яблоко", "груша", "банан", "апельсин");
        List<String> sortedStrings = strings.stream().sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder())).collect(Collectors.toList());
        System.out.println("Отсортированные строки: " + sortedStrings);

        // Самое длинное слово в массиве строк
        List<String[]> arrayOfStrings = Arrays.asList(
                "яблоко груша банан апельсин".split(" "),
                "мандарин киви виноград".split(" ")
        );
        String longestWordInArray = arrayOfStrings.stream()
                .flatMap(Arrays::stream)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
        System.out.println("Самое длинное слово в массиве: " + longestWordInArray);
    }
}
