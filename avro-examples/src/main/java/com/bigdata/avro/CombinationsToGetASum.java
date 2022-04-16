package com.bigdata.avro;

import java.util.Scanner;

public class CombinationsToGetASum {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a Number :: ");
        int sum = scanner.nextInt();
        int minSumValue = 1 + 2;
        int maxSumValue = 50 + 49;
        if(sum > maxSumValue || sum < minSumValue) {
            System.out.println("Please enter the correct sum value. The sum value should be in between 3 to 99");
            System.exit(-1);
        }
        System.out.println("The possible combination for 2 numbers to get sum = " + sum + " are :: ");
        for (int i = 1; i <= 50; i++) {
            for (int j = 2; j != i && j < 50; j++) {
                if((i + j) == sum) {
                    System.out.println(i + ", " + j);
                }
            }

        }

    }
}
