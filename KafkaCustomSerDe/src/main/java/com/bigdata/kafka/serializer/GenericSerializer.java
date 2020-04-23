package com.bigdata.kafka.serializer;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;

public class GenericSerializer<T> implements Serializer<T> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, T t) {
        return SerializationUtils.serialize((Serializable) t);
    }

    @Override
    public void close() {

    }
}
