package com.bigdata.avro;

import java.util.Scanner;

public class PerfectNumber {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a Number :: ");
        int number = scanner.nextInt();
        isPerfectNumber1(number);
        isPerfectNumber2(number);
    }

    public static void isPerfectNumber1(int number) {
        String numberAsString = Integer.toString(number);
        int total = 0;
        for (char c : numberAsString.toCharArray()) {
            int digit = Integer.parseInt(String.valueOf(c));
            total = total + digit;
        }

        System.out.println("Sum of digits in number - " + number + " is : " + total);
        if(total % 5 == 0) {
            System.out.println("Perfect Number!");
        } else {
            System.out.println("Not a Perfect Number!");
        }
    }

    public static void isPerfectNumber2(int number) {
        int total = 0;
        int tmp = number;
        while(number / 10 != 0) {
            int remainder = number % 10;
            total = total + remainder;

            number = number / 10;
        }

        total = total + number;

        System.out.println("Sum of digits in number - " + tmp + " is : " + total);
        if(total % 5 == 0) {
            System.out.println("Perfect Number!");
        } else {
            System.out.println("Not a Perfect Number!");
        }
    }
}
