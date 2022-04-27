#!/bin/sh
#set -e

EDGAR_LOGS_CONFIG_FILE=/edgar/edgar-avro-config.properties
sed -i -e "s/BOOTSTRAP_SERVERS/$BOOTSTRAP_SERVERS/"g $EDGAR_LOGS_CONFIG_FILE
sed -i -e "s/SCHEMA_REGISTRY_URL/$SCHEMA_REGISTRY_URL/"g $EDGAR_LOGS_CONFIG_FILE

export KAFKA_OPTS="-Djava.security.auth.login.config=/home/bigdata/kerberos/kafka_client_jaas.conf"
export SCHEMA_REGISTRY_OPTS="-Djava.security.auth.login.config=/home/bigdata/kerberos/kafka_client_jaas.conf"

echo "Starting Java Program to produce logs messages to Kafka Cluster..."
java \
  -Djava.security.auth.login.config=/edgar/kafka_client_jaas.conf \
  -cp /java_examples/KafkaExamples-1.0-SNAPSHOT.jar \
  com.bigdata.kafka.producer.file.dynamic.EdgarLogsKafkaDynamicFileProducer \
  /edgar/edgar-avro-config.properties