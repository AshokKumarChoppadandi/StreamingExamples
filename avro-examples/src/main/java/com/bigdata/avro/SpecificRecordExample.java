package com.bigdata.avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

/**
 * @author Ashok Kumar Choppadandi
 *
 * Avro Examples:
 * --------------
 *
 * Secific Record Example
 *
 * Note: Specific Record will have COMPILE TIME SAFETY
 */
public class SpecificRecordExample {
    public static void main(String[] args) {
        // Step 1: Create a Specific Record
        Customer.Builder customerBuilder = Customer.newBuilder();
        customerBuilder.setFirstName("Alice");
        customerBuilder.setLastName("Tape");
        customerBuilder.setAge(31);
        customerBuilder.setHeight(176.5f);
        customerBuilder.setWeight(78.4f);
        customerBuilder.setAutomatedEmail(false);

        Customer customer = customerBuilder.build();
        System.out.println("Customer :: " + customer);

        // Step 2: Write Specific Record to a File
        DatumWriter<Customer> datumWriter = new SpecificDatumWriter<>(Customer.class);
        String outputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers-specific.avro";
        File avroOutputFile = new File(outputFile);

        try {
            DataFileWriter<Customer> writer = new DataFileWriter<>(datumWriter);
            writer.create(customer.getSchema(), avroOutputFile);
            writer.append(customer);
            writer.flush();
            System.out.println("File Written successfully!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Step 3: Read Specific Record from a File
        String inputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers-specific.avro";
        File inputAvroFile = new File(inputFile);

        DatumReader<Customer> datumReader = new SpecificDatumReader<>(Customer.class);
        try {
            DataFileReader<Customer> reader = new DataFileReader<>(inputAvroFile, datumReader);
            while (reader.hasNext()) {
                // Step 4: Interpret the Specific Record
                Customer customer1 = reader.next();
                System.out.println("Record :: ");
                System.out.println("First Name :: " + customer1.getFirstName() +
                        "\nLast Name :: " + customer1.getLastName() +
                        "\nAge :: " + customer1.getAge() +
                        "\nHeight :: " + customer1.getHeight() +
                        "\nWeight :: " + customer1.getWeight() +
                        "\nAutomated Email :: " + customer1.getAutomatedEmail());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
