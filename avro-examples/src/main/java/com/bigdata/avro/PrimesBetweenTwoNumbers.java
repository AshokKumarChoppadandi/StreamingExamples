package com.bigdata.avro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimesBetweenTwoNumbers {
    public static void main(String[] args) {
        int number1 = 30;
        int number2 = 60;
        List<Integer> numbers = IntStream.rangeClosed(number1, number2).boxed().collect(Collectors.toList());

        List<Integer> primeNumbers = new ArrayList<>();
        for (Integer integer : numbers) {
            int counter = 0;
            int number = integer;
            if (number <= 2) {
                primeNumbers.add(number);
            } else {
                for (int j = 1; j <= number; j++) {
                    if (number % j == 0) {
                        counter++;
                    }
                }
            }
            if (counter == 2) {
                primeNumbers.add(number);
            }
        }

        System.out.println(numbers);
        System.out.println(primeNumbers);
    }
}
