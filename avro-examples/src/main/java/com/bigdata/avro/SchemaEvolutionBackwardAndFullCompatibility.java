package com.bigdata.avro;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;

public class SchemaEvolutionBackwardAndFullCompatibility {
    public static void main(String[] args) {
        CustomerV1.Builder builder = CustomerV1.newBuilder();
        builder.setFirstName("Alice");
        builder.setLastName("Tape");
        builder.setAge(31);
        builder.setHeight(176.5f);
        builder.setWeight(78.4f);
        builder.setAutomatedEmail(false);

        CustomerV1 customerV1 = builder.build();
        System.out.println("Customer with V1 schema\n" + customerV1);

        System.out.println("Writing data using Customer V1 Schema.");
        DatumWriter<CustomerV1> datumWriter = new SpecificDatumWriter<>(CustomerV1.class);
        String outputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers-v1.avro";
        File outputAvroFile = new File(outputFile);
        try {
            DataFileWriter<CustomerV1> writer = new DataFileWriter<>(datumWriter);
            writer.create(customerV1.getSchema(), outputAvroFile);
            writer.append(customerV1);
            writer.flush();
            writer.close();
            System.out.println("Data Written Successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Reading data using Customer V2 Schema.");
        DatumReader<CustomerV2> datumReader = new SpecificDatumReader<>(CustomerV2.class);
        File inputAvroFile = outputAvroFile;
        try {
            DataFileReader<CustomerV2> reader = new DataFileReader<>(inputAvroFile, datumReader);
            System.out.println();
            for (CustomerV2 customerV2 : reader) {
                System.out.println("Customer with V2 schema\n" + customerV2);
                /**
                System.out.println("First Name :: " + customerV2.getFirstName() +
                        "\nLast Name :: " + customerV2.getLastName() +
                        "\nAge :: " + customerV2.getAge() +
                        "\nHeight :: " + customerV2.getHeight() +
                        "\nWeight :: " + customerV2.getWeight() +
                        "\nPhone :: " + customerV2.getPhone() +
                        "\nEmail :: " + customerV2.getEmail()
                );
                */
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
