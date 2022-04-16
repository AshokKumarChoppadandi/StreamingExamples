package com.bigdata.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.*;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

import java.io.File;

/**
 * Avro Examples:
 * --------------
 *
 * Generic Record Example
 *
 * Note: Generic Record doesn't have any COMPILE TIME SAFETY
 */

public class GenericRecordExample {
    public static void main(String[] args) {
        String schemaString = "{\n" +
                "    \"type\": \"record\",\n" +
                "    \"namespace\": \"com.bigdata.avro.example\",\n" +
                "    \"name\": \"Customer\",\n" +
                "    \"doc\": \"Avro Schema for Customer\",\n" +
                "    \"fields\": [\n" +
                "        { \"name\": \"first_name\", \"type\": \"string\", \"doc\": \"First Name of the Customer\" },\n" +
                "        { \"name\": \"last_name\", \"type\": \"string\", \"doc\": \"Last Name of the Customer\" },\n" +
                "        { \"name\": \"age\", \"type\": \"int\", \"doc\": \"Age of the Customer\" },\n" +
                "        { \"name\": \"height\", \"type\": \"float\", \"doc\": \"Height of the Customer in cms\" },\n" +
                "        { \"name\": \"weight\", \"type\": \"float\", \"doc\": \"Weight of the Customer in kgs\" },\n" +
                "        { \"name\": \"automated_email\", \"type\": \"boolean\", \"default\": true, \"doc\": \"true if user wants an automated email\" }\n" +
                "    ]\n" +
                "}";
        // Step 1: Defining the Schema
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(schemaString);

        // Step 2: Creating the Generic Record
        GenericRecordBuilder builder = new GenericRecordBuilder(schema);
        builder.set("first_name", "Alice");
        builder.set("last_name", "Tape");
        builder.set("age", 31);
        builder.set("height", 178.5f);
        builder.set("weight", 78.4f);
        builder.set("automated_email", false);

        GenericData.Record record = builder.build();
        System.out.println("Record :: " + record);

        // Step 3: Writing the Generic Record to a File
        String outputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers.avro";
        File avroOutputFile = new File(outputFile);
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try {
            DataFileWriter<GenericRecord> writer = new DataFileWriter<>(datumWriter);
            writer.create(schema, avroOutputFile);
            writer.append(record);
            writer.flush();
            System.out.println("Data written to file!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Step 4: Read a Generic record from a File
        String inputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers.avro";
        File inputAvroFile = new File(inputFile);
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        try {
            DataFileReader<GenericRecord> reader1 = new DataFileReader<>(inputAvroFile, datumReader);
            while (reader1.hasNext()) {
                GenericRecord record1 = reader1.next();
                // Step 5: Interpret the Generic Record
                System.out.println("Record :: ");
                String firstName = record1.get("first_name").toString();
                String lastName = record1.get("last_name").toString();
                int age = Integer.parseInt(record1.get("age").toString());
                float height = Float.parseFloat(record1.get("height").toString());
                float weight = Float.parseFloat(record1.get("weight").toString());
                boolean automatedEmail = Boolean.parseBoolean(record1.get("automated_email").toString());

                System.out.println("First Name :: " + firstName +
                        "\nLast Name :: " + lastName +
                        "\nAge :: " + age +
                        "\nHeight :: " + height +
                        "\nWeight :: " + weight +
                        "\nAutomated Email :: " + automatedEmail);
            }
            reader1.close();
            System.out.println("Read from file completed!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
