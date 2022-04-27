FROM openjdk:8

MAINTAINER AshokKumarChoppadandi ashokkumar98778@gmail.com

WORKDIR /
RUN mkdir /edgar /java_examples
COPY maven/KafkaExamples-1.0-SNAPSHOT.jar /java_examples/
COPY configs/* /edgar/
COPY edgar-logs-entrypoint.sh /

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get -qq update \
    && apt-get -yqq install krb5-user libpam-krb5 \
    && apt-get -yqq clean \
    && rm -rf /etc/krb5.conf \
    && mv /edgar/krb5.conf /etc/krb5.conf \
    && chmod +x /edgar-logs-entrypoint.sh \
    && chmod +x /edgar/edgar-avro-config.properties

ENV BOOTSTRAP_SERVERS SASL_SSL:\/\/worker1.bigdata.com:9094,SASL_SSL:\/\/worker2.bigdata.com:9094,SASL_SSL:\/\/worker3.bigdata.com:9094
ENV SCHEMA_REGISTRY_URL http:\/\/admin2.bigdata.com:8081

ENTRYPOINT ["/edgar-logs-entrypoint.sh"]
CMD ["sh"]