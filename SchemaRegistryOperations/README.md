```
kafka-avro-console-producer \
  --broker-list worker1.bigdata.com:9092,worker2.bigdata.com:9092,worker3.bigdata.com:9092 \
  --topic Test --property schema.registry.url=http://schema-registry:8081 \
  --property value.schema.id=2
```

```
kafka-avro-console-consumer \
  --bootstrap-server worker1.bigdata.com:9092,worker2.bigdata.com:9092,worker3.bigdata.com:9092 \
  --topic Test \
  --from-beginning \
  --property schema.registry.url="http://schema-registry:8081"
```