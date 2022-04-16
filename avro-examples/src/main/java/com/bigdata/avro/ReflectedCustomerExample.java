package com.bigdata.avro;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;

import java.io.File;
import java.io.IOException;

public class ReflectedCustomerExample {
    public static void main(String[] args) {
        // Generating Schema from Java Class
        Schema schema = ReflectData.get().getSchema(ReflectedCustomer.class);
        System.out.println("Schema :: " + schema.toString(true));

        // Generating an Avro Object

        ReflectedCustomer reflectedCustomer = new ReflectedCustomer(
                "Alice",
                null,
                31,
                176.5f,
                78.4f,
                Boolean.parseBoolean(schema.getField("automatedEmail").defaultVal().toString())
        );
        System.out.println("Reflected Customer :: " + reflectedCustomer);

        // Creating a File with Customer using Reflected Schema
        String outputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers-reflected.avro";
        File outputAvroFile = new File(outputFile);
        DatumWriter<ReflectedCustomer> datumWriter = new ReflectDatumWriter<>(ReflectedCustomer.class);
        try {
            DataFileWriter<ReflectedCustomer> writer = new DataFileWriter<>(datumWriter);
            writer.setCodec(CodecFactory.deflateCodec(9));
            writer.create(schema, outputAvroFile);
            writer.append(reflectedCustomer);
            writer.flush();
            writer.close();
            System.out.println("File Written Successfully!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read an Avro File using Reflected Schema
        String inputFile = "C:\\Users\\lenovo\\IdeaProjects\\StreamingExamples\\avro-examples\\src\\main\\resources\\customers-reflected.avro";
        File inputAvroFile = new File(inputFile);
        DatumReader<ReflectedCustomer> datumReader = new ReflectDatumReader<>(ReflectedCustomer.class);
        try {
            DataFileReader<ReflectedCustomer> reader = new DataFileReader<>(inputAvroFile, datumReader);
            for (ReflectedCustomer reflectedCustomer1 : reader) {
                System.out.println(
                        "FirstName :: " + reflectedCustomer1.getFirstName() +
                        "\nLastName :: " + reflectedCustomer1.getLastName() +
                        "\nAge :: " + reflectedCustomer1.getAge() +
                        "\nHeight :: " + reflectedCustomer1.getHeight() +
                        "\nWeight :: " + reflectedCustomer1.getWeight() +
                        "\nAutomatedEmail :: " + reflectedCustomer1.isAutomatedEmail()
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
