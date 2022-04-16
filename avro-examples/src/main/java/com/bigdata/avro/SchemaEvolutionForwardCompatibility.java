package com.bigdata.avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

public class SchemaEvolutionForwardCompatibility {
    public static void main(String[] args) {
        CustomerV2.Builder builder = CustomerV2.newBuilder();
        builder.setFirstName("Alice");
        builder.setLastName("Tape");
        builder.setAge(31);
        builder.setHeight(176.5f);
        builder.setWeight(78.4f);
        builder.setPhone("9876543210");
        builder.setEmail("xyz@abc.com");

        CustomerV2 customerV2 = builder.build();
        System.out.println("Customer with V2 schema\n" + customerV2);

        System.out.println("Writing data using Customer V2 Schema.");
        DatumWriter<CustomerV2> datumWriter = new SpecificDatumWriter<>(CustomerV2.class);
        String outputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers-v2.avro";
        File outputAvroFile = new File(outputFile);
        try {
            DataFileWriter<CustomerV2> writer = new DataFileWriter<>(datumWriter);
            writer.create(customerV2.getSchema(), outputAvroFile);
            writer.append(customerV2);
            writer.flush();
            writer.close();
            System.out.println("Data Written Successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Reading data using Customer V1 Schema.");
        DatumReader<CustomerV1> datumReader = new SpecificDatumReader<>(CustomerV1.class);
        File inputAvroFile = outputAvroFile;
        try {
            DataFileReader<CustomerV1> reader = new DataFileReader<>(inputAvroFile, datumReader);
            for (CustomerV1 customerV1 : reader) {
                System.out.println(customerV1);
                /**
                System.out.println("First Name :: " + customerV1.getFirstName() +
                        "\nLast Name :: " + customerV1.getLastName() +
                        "\nAge :: " + customerV1.getAge() +
                        "\nHeight :: " + customerV1.getHeight() +
                        "\nWeight :: " + customerV1.getWeight() +
                        "\nAutomated Email :: " + customerV1.getAutomatedEmail()
                );
                */
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
