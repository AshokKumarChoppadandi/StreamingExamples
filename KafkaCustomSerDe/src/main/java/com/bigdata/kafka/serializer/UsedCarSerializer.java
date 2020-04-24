package com.bigdata.kafka.serializer;

import com.bigdata.kafka.models.UsedCar;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class UsedCarSerializer implements Serializer<UsedCar> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, UsedCar usedCar) {
        return SerializationUtils.serialize(usedCar);
    }

    @Override
    public void close() {

    }
}
