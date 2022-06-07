FROM openjdk:8

MAINTAINER AshokKumarChoppadandi ashokkumar98778@gmail.com

WORKDIR /
RUN mkdir /edgar /java_examples /secure
COPY maven/KafkaExamples-1.0-SNAPSHOT.jar /java_examples/

COPY configs/kdc/krb5.conf /etc/krb5.conf
COPY configs/kafka_client/* /secure/
COPY configs/edgar.avsc /edgar/
COPY configs/edgar-avro-config.properties /edgar/

COPY edgar-logs-entrypoint.sh /

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get -qq update \
    && apt-get -yqq install krb5-user libpam-krb5 \
    && apt-get -yqq clean \
    && chmod +x /edgar-logs-entrypoint.sh \
    && chmod +x /edgar/edgar-avro-config.properties

# KAFKA PROPERTIES
ENV BOOTSTRAP_SERVERS localhost:9092
ENV ACKS all
ENV RETRIES 0
ENV BATCH_SIZE 16384
ENV LINGER_MS 1
ENV BUFFER_MEMORY 33554432
ENV KEY_SERIALIZER org.apache.kafka.common.serialization.StringSerializer
ENV VALUE_SERIALIZER org.apache.kafka.common.serialization.StringSerializer
ENV SCHEMA_REGISTRY_URL "http:\/\/localhost:8081"
ENV SCHEMA_FILE_LOCATION "\/edgar\/edgar.avsc"
ENV OUTPUT_FORMAT avro
ENV TOPIC_NAME logs-test
ENV INPUT_LOGS_DIR "\/edgar\/input_logs\/"
ENV ENABLE_IDEMPOTENCE "true"

# SASL_SSL / GSSAPI (SSL & KEREROS AUTHENTICATION) PROPERTIES
ENV SSL_KEYSTORE_LOCATION "/secure/kafka.client.keystore.jks"
ENV SSL_KEY_PASSWORD clientpassword
ENV SSL_KEYSTORE_PASSWORD clientpassword
ENV SSL_TRUSTSTORE_LOCATION "/secure/kafka.client.truststore.jks"
ENV SSL_TRUSTSTORE_PASSWORD clientpassword
ENV SECURITY_PROTOCOL SASL_SSL
ENV SASL_KERBEROS_SERVICE_NAME kafka

# KERBEROS SERVER AND PRINCIPALS CONFIGURATION
# KDC & KERBEROS ADMIN SERVERS
ENV KRB5_CONF_FILE /etc/krb5.conf
ENV KDC_HOSTNAME admin1.bigdata.com
ENV KERBEROS_ADMIN_SERVER admin1.bigdata.com

# JAAS CONFIGURATION
ENV KAFKA_CLIENT_JAAS_FILE "/secure/kafka_client_jaas.conf"
ENV KAFKA_CLIENT_USER_KEYTAB "\/secure\/kafka_client.user.keytab"
ENV KAFKA_CLIENT_KERBEROS_PRINICIPAL kafka@KAFKA.SECURE

# EXTRA PROPERTIES TO DECIDE THE CONFIGURATION
ENV CONFIG_FILE_PATH /edgar/edgar-avro-config.properties
ENV IS_CONFIGURING_PROPERTIES_REQUIRED yes
ENV IS_KERBEROS_AUTHENTICATION_REQUIRED yes

# JAVA / KAFKA PRODUCER APPLICATION PROPERTIES
ENV JAR_FILE_LOCATION /java_examples/
ENV JAR_NAME KafkaExamples-1.0-SNAPSHOT.jar
ENV RUN_CLASS com.bigdata.kafka.producer.file.dynamic.EdgarLogsKafkaDynamicFileProducer

ENTRYPOINT ["/edgar-logs-entrypoint.sh"]
CMD ["sh"]