#!/bin/sh
#set -e

check_config_file() {
  echo "Kafka Producer Configuration file is : $CONFIG_FILE_PATH"
  if [ ! -f "$CONFIG_FILE_PATH" ]; then
      echo "$CONFIG_FILE_PATH doesn't exists. Exiting the job!"
      exit 1
  fi
}

configure_kafka_producer_properties() {
  echo "Configuring Kafka Properties..."
  sed -i -e "s/BOOTSTRAP_SERVERS/$BOOTSTRAP_SERVERS/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/ACKS/$ACKS/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/RETRIES/$RETRIES/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/BATCH_SIZE/$BATCH_SIZE/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/LINGER_MS/$LINGER_MS/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/BUFFER_MEMORY/$BUFFER_MEMORY/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/KEY_SERIALIZER/$KEY_SERIALIZER/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/VALUE_SERIALIZER/$VALUE_SERIALIZER/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/SCHEMA_REGISTRY_URL/$SCHEMA_REGISTRY_URL/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/SCHEMA_FILE_LOCATION/$SCHEMA_FILE_LOCATION/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/OUTPUT_FORMAT/$OUTPUT_FORMAT/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/TOPIC_NAME/$TOPIC_NAME/"g "$CONFIG_FILE_PATH"
  sed -i -e "s/INPUT_LOGS_DIR/$INPUT_LOGS_DIR/"g "$CONFIG_FILE_PATH"
}

configure_kafka_producer_properties_for_kerberos_authentication() {
  echo "Configuring Kafka Properties for Kerberos Authentication..."

  echo "ssl.keystore.location=$SSL_KEYSTORE_LOCATION" >> "$CONFIG_FILE_PATH"
  echo "ssl.key.password=$SSL_KEY_PASSWORD" >> "$CONFIG_FILE_PATH"
  echo "ssl.keystore.password=$SSL_KEYSTORE_PASSWORD" >> "$CONFIG_FILE_PATH"
  echo "ssl.truststore.location=$SSL_TRUSTSTORE_LOCATION" >> "$CONFIG_FILE_PATH"
  echo "ssl.truststore.password=$SSL_TRUSTSTORE_PASSWORD" >> "$CONFIG_FILE_PATH"
  echo "security.protocol=$SECURITY_PROTOCOL" >> "$CONFIG_FILE_PATH"
  echo "sasl.kerberos.service.name=$SASL_KERBEROS_SERVICE_NAME" >> "$CONFIG_FILE_PATH"
}


configure_ssl_Kerberos_properties() {
  echo "Kerberos Configuration file is - $KRB5_CONF_FILE"
  echo "Configuring Kerberos Properties..."
  sed -i -e "s/KDC_HOSTNAME/$KDC_HOSTNAME/"g "$KRB5_CONF_FILE"
  sed -i -e "s/KERBEROS_ADMIN_SERVER/$KERBEROS_ADMIN_SERVER/"g "$KRB5_CONF_FILE"
}

configure_kafka_client_jaas() {
  echo "Kafka Client JAAS file is - $KAFKA_CLIENT_JAAS_FILE"
  echo "Configuring Kafka Client JAAS Properties :"
  sed -i -e "s/KAFKA_CLIENT_USER_KEYTAB/$KAFKA_CLIENT_USER_KEYTAB/"g "$KAFKA_CLIENT_JAAS_FILE"
  sed -i -e "s/KAFKA_CLIENT_KERBEROS_PRINICIPAL/$KAFKA_CLIENT_KERBEROS_PRINICIPAL/"g "$KAFKA_CLIENT_JAAS_FILE"
}

is_properties_configuration_required () {
  if [ "$IS_CONFIGURING_PROPERTIES_REQUIRED" = "yes" ]; then
      configure_kafka_producer_properties
  else
      echo "Kafka Producer Properties are already defined in the configuration file."
  fi
}

is_kerberos_authentication_required() {
  if [ "$IS_KERBEROS_AUTHENTICATION_REQUIRED" = "yes" ]; then
        configure_ssl_Kerberos_properties
        configure_kafka_client_jaas
        configure_kafka_producer_properties_for_kerberos_authentication
    else
        echo "No Kerberos Authentication is required!"
    fi
}

check_config_file
is_properties_configuration_required
is_kerberos_authentication_required

echo "Final Kafka Producer Configuration Properties are :: "
cat "$CONFIG_FILE_PATH"

export KAFKA_OPTS="-Djava.security.auth.login.config=$KAFKA_CLIENT_JAAS_FILE"
export SCHEMA_REGISTRY_OPTS="-Djava.security.auth.login.config=$KAFKA_CLIENT_JAAS_FILE"

echo "Starting Java Program to produce logs messages to Kafka Cluster..."
java \
  -Djava.security.auth.login.config="$KAFKA_CLIENT_JAAS_FILE" \
  -cp "$JAR_FILE_LOCATION""$JAR_NAME" \
  "$RUN_CLASS" \
  "$CONFIG_FILE_PATH"