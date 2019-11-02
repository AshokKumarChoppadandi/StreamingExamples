package com.bigdata.kafka.producer.types;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class MyProducerCallback implements Callback {

    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if(e != null)
            System.out.println("PRODUCER FAILED WITH AN EXCEPTION :: " + e);
        else
            System.out.println("MESSAGE SENT SUCCESSFULLY...!!!");
    }
}
