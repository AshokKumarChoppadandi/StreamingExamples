package com.bigdata.kafka.producer.edgar_logs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ReadEDGARLog2 {
    public static void main(String[] args) {
        String filePath = args[0];
        String outputDirectoryLocation = "/home/ashok/IdeaProjects/StreamingExamples/KafkaExamples/src/main/resources/edgar_logs_splitted";
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        Scanner scanner = null;

        String outputFile = null;
        long totalLines = 0L;
        long numberOfLinesPerFile = 5000000L;
        try {
            fileInputStream = new FileInputStream(filePath);
            scanner = new Scanner(fileInputStream, "UTF-8");
            // To skip the header
            scanner.nextLine(); // 23677601
            while (scanner.hasNextLine()) {
                if(totalLines % numberOfLinesPerFile == 0) {
                    if(fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        System.out.println(numberOfLinesPerFile + " lines written successfully to the file " + outputFile);
                    }
                    long timeInMilliSeconds = System.currentTimeMillis();
                    outputFile = outputDirectoryLocation + "/edgar_log_" + timeInMilliSeconds + ".csv";
                    fileOutputStream = new FileOutputStream(outputFile);
                }
                /**
                 if(totalLines == 0) {
                 long timeInMilliSeconds = System.currentTimeMillis();
                 outputFile = outputDirectoryLocation + "/" + timeInMilliSeconds;
                 fileOutputStream = new FileOutputStream(outputFile);
                 }
                 */
                String outputLine = scanner.nextLine() + "\n";
                fileOutputStream.write(outputLine.getBytes(StandardCharsets.UTF_8));
                // fileOutputStream.write("\n".getBytes(StandardCharsets.UTF_8));
                totalLines += 1;

                if(totalLines == 100) {
                    System.out.println("100 Lines written successfully to an output file");
                    break;
                }

                /**
                 if(totalLines % 10000000 == 0) {
                 fileOutputStream.flush();
                 fileOutputStream.close();
                 System.out.println(totalLines + " lines written successfully to the file " + outputFile);
                 totalLines = 0;
                 }*/

            }
            if(fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
                System.out.println(totalLines % numberOfLinesPerFile + " lines written successfully to the file " + outputFile);
            }

        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scanner != null) {
                scanner.close();
            }
        }
        System.out.println("Total Number of Lines :: " + totalLines);
    }
}
