# Real-time Data Processing with Kafka Eco-system Tools

## Introduction

## Pre-requisites

- docker
- docker-compose
- vm.max_map_count=262144

## Execution Steps

### Setting up DEV Environment

Spin up the Docker Containers for setting up the Dev Environment using this docker-compose file.

```
docker-compose -f docker-compose-hadoop-kafka-es-2.yaml up -d
```

### Check the status of the containers

```
docker-compose -f docker-compose-hadoop-kafka-es-2.yaml ps -a
```

### Check all the services are up and running

#### To Check Hadoop

- Namenode: `http://localhost:50070`

- Resource Manager: `http://localhost:8088`

- History Server: `http://localhost:19888`

- Hive Server 2: `http://localhost:10002`

#### To Check Kafka & Tools 

- Schema Registry: `http://localhost:8081`

- Kafka Connect: `http://localhost:8083`

- KSQL: `http://localhost:18088`

Login to Kafka Container:

```
docker exec -it broker sh
```

Execute below commands to test Kafka

```
# To Get the Topics
kafka-topics --bootstrap-server broker:9092 --list

# To Create a Kafka Topic
kafka-topics --bootstrap-server broker:9092 --create --topic first-topic --partitions 3 --replication-factor 1

# Produce some sample messages
kafka-console-producer --bootstrap-server broker:9092 --topic first-topic

> Hello
> Hello Again
> This a message from Kafka Console Producer
> Stopping!
>

# Consume the messages
kafka-console-consumer --bootstrap-server broker:9092 --topic first-topic --from-beginning
```

Execute below commands to test Kafka with Avro Data

```
# To Get the Topics
kafka-topics --bootstrap-server broker:9092 --list

# To Create a Kafka Topic
kafka-topics --bootstrap-server broker:9092 --create --topic employee --partitions 3 --replication-factor 1

# Produce some sample AVRO messages
kafka-avro-console-producer --bootstrap-server broker:9092 --topic employee --property schema.registry.url=http://schemaregistry:8081 --property value.schema='{
    "namespace": "com.bigdata.kafka.employee",
    "type": "record",
    "name": "Employee",
    "description": "Employee Details",
    "fields": [
        {"name": "eid", "type": "int"},
        {"name": "ename", "type": "string"},
        {"name": "esalary", "type": "int"},
        {"name": "edept", "type": "string"},
        {"name": "eage", "type": "int"}
    ]
}'

{"eid": 111, "ename": "Alice", "esalary": 10000, "edept": "IT", "eage": 25}
{"eid": 112, "ename": "Bob", "esalary": 13000, "edept": "CSE", "eage": 24}
{"eid": 113, "ename": "Charlie", "esalary": 11000, "edept": "MECH", "eage": 22}
{"eid": 114, "ename": "Dave", "esalary": 12000, "edept": "IT", "eage": 27}

# Consume the AVRO messages
kafka-avro-console-consumer --bootstrap-server broker:9092 --topic employee --from-beginning --property schema.registry.url=http://schemaregistry:8081
```

#### To check Elasticsearch & Kibana

- Elasticsearch: `http://localhost:9200`
- Kibana: `http://localhost:5601`

### Produce Some Log Messages

Load some sample log files into the Volume Directory of `edgar-logs-kafka-producer` container (defined inside the docker-compose file)

```
volumes:
      - ./input_logs/:/edgar/input_logs/:rw
```

Log files can be downloaded from here

***Note:***

*`Once the logs files are placed into the Volume directory, the Java Application which is running inside docker container will read the logs files and send it to Kafka Topic`*

### Data Processing / Analytics using KSQL

#### To Lauch KSQL Shell

```
# Logging into the container
docker exec -it ksql sh

# Logging into KSQL Shell
ksql http://ksql:8088
```

#### Sample KSQL Queries


##### To Print the list of Topics in Kafka Cluster
```
ksql> LIST TOPICS;
```

##### To Print Messages from a Kafka Topic
```
ksql> PRINT 'first-topic';
```

##### To Print Messages from a Kafka Topic from beginning
```
ksql> PRINT 'first-topic' FROM BEGINNING;
```

##### To Print Messages from a Kafka Topic from beginning and limit
```
ksql> PRINT 'first-topic' FROM BEGINNING LIMIT 5;
```

##### To Print the list of Kafka STREAMS
```
ksql> LIST STREAMS;
```

##### To Print the list of Kafka TABLES
```
ksql> LIST TABLES;
```

##### To Move Offsets to Earliest
```
SET 'auto.offset.reset'='earliest';
```

##### CREATE STREAM on a Kafka Topic
```
ksql> CREATE STREAM EDGAR_LOGS_STREAM WITH (
    KAFKA_TOPIC='logs-test',
    VALUE_FORMAT='AVRO'
);
```

##### To DESCRIBE a Stream
```
ksql> DESCRIBE EDGAR_LOGS_STREAM;
ksql> DESCRIBE EXTENDED EDGAR_LOGS_STREAM;
```

##### READ data from Stream
```
ksql> SELECT * FROM EDGAR_LOGS_STREAM EMIT CHANGES;
```

##### Creating a STREAM from another Stream (CSAS - CREATE STREAM AS SELECT)
```
ksql> CREATE STREAM EDGAR_LOGS_FORMATTED WITH (
  KAFKA_TOPIC = 'edgar-logs-formatted',
  VALUE_FORMAT = 'AVRO',
  PARTITIONS = 3,
  REPLICAS = 1
) AS 
select 
  IP AS IP_ADDRESS, 
  `DATE` AS REQUEST_DATE, 
  `TIME` AS REQUEST_TIME,
  GET_WINDOW(`DATE` + ' ' + `TIME`, 5) AS FIVE_MINUTE_WINDOW,
  CASE 
    WHEN ZONE IS NOT NULL THEN CAST( CAST( ZONE AS DOUBLE ) AS INT )
    ELSE -1
  END AS ZONE,
  CASE 
    WHEN CIK IS NOT NULL THEN CAST( CAST( CIK AS DOUBLE ) AS BIGINT )
    ELSE CAST( '-1' AS BIGINT )
  END AS CIK,
  ACCESSION,
  EXTENTION,
  CASE 
    WHEN CODE IS NOT NULL THEN CAST( CAST( CODE AS DOUBLE ) AS INT)
    ELSE -1
  END AS RESPONSE_CODE,
  CASE 
    WHEN `SIZE` IS NOT NULL THEN CAST( CAST( `SIZE` AS DOUBLE ) AS INT )
    ELSE -1
  END AS RESPONSE_BYTES_SIZE,
  CASE 
    WHEN IDX IS NOT NULL THEN CAST( IDX AS DOUBLE)
    ELSE CAST('-1.0' AS DOUBLE)
  END AS IDX,
  CASE 
    WHEN NOREFER IS NOT NULL THEN CAST (NOREFER AS DOUBLE)
    ELSE CAST('-1.0' AS DOUBLE)
  END AS NOREFER,
  CASE 
    WHEN NOAGENT IS NOT NULL THEN CAST (NOAGENT AS DOUBLE)
    ELSE CAST('-1.0' AS DOUBLE)
  END AS NOAGENT,
  CASE 
    WHEN FIND IS NOT NULL THEN CAST (FIND AS DOUBLE)
    ELSE CAST('-1.0' AS DOUBLE)
  END AS FIND,
  CASE 
    WHEN CRAWLER IS NOT NULL THEN CAST (CRAWLER AS DOUBLE)
    ELSE CAST('-1.0' AS DOUBLE)
  END AS CRAWLER,
  BROWSER
FROM EDGAR_LOGS_STREAM EMIT CHANGES;
```

##### Describe the STREM
```
ksql> DESCRIBE EXTENDED EDGAR_LOGS_FORMATTED;
```

##### Explain QUERY

```
ksql> EXPLAIN CSAS_EDGAR_LOGS_FORMATTED_0;
```

##### Read the data from EDGAR_LOGS_FORMATTED Stream
```
ksql> SELECT * FROM EDGAR_LOGS_FORMATTED EMIT CHANGES;
```

##### Create TABLE to store the Aggregated logs
```
ksql> CREATE TABLE AGGREGATED_EDGAR_LOGS WITH (
  KAFKA_TOPIC = 'aggregated-edgar-logs',
  VALUE_FORMAT = 'AVRO',
  PARTITIONS = 3,
  REPLICAS = 1
) AS
SELECT FIVE_MINUTE_WINDOW, ZONE, RESPONSE_CODE, COUNT(*) AS NUMBER_OF_REQUESTS, SUM(RESPONSE_BYTES_SIZE) AS BYTES_SERVED
FROM EDGAR_LOGS_FORMATTED
GROUP BY FIVE_MINUTE_WINDOW, ZONE, RESPONSE_CODE;
```

##### Describe the TABLE
```
ksql> DESCRIBE EXTENDED AGGREGATED_EDGAR_LOGS;
```

##### Read the Messages from Kafka TABLE
```
ksql> SELECT * FROM AGGREGATED_EDGAR_LOGS EMIT CHANGES;
```

### To Send / Load / Sink data to the External Systems like PostgrSQL, Hadoop & Elasticsearch

#### Kafka JDBC Sink Connector to load data from Kafka to PostgreSQL

##### Write the PostgreSQL Sink Connector in a JSON file
```
cat > edgar-logs-pg-sink.json
{
    "name": "edgar-logs-pg-sink",
    "config": {
        "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
        "dialect.name": "PostgreSqlDatabaseDialect",
        "insert.mode": "upsert",
        "batch.size": 3000,
        "table.name.format": "${topic}",
        "pk.mode": "kafka",
        "pk.fields": "__connect_topic,__connect_partition,__connect_offset",
        "fields.whitelist": "",
        "db.timezone": "UTC",
        "connection.url": "jdbc:postgresql://postgres:5432/kafkadb",
        "connection.user": "hadoop",
        "connection.password": "Password@123",
        "auto.create": "true",
        "auto.evolve": "true",
        "topics": "aggregated-edgar-logs",
        "tasks.max": "5",
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
		"value.converter": "io.confluent.connect.avro.AvroConverter",
		"value.converter.schema.registry.url": "http://schemaregistry:8081",
		"value.converter.schemas.enable": "true"
    }
}
```

##### Launch the POSTGRESQL Sink Connector
```
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d @edgar-logs-pg-sink.json http://localhost:8083/connectors
```

##### To check the status of the Connector

- List of connectors - http://localhost:8083/connectors
- Status of the Connector - http://localhost:8083/connectors/edgar-logs-pg-sink
- Status of the tasks of the Connector - http://localhost:8083/connectors/edgar-logs-pg-sink/status

#### Elasticsearch Sink Connector to load data from Kafka to Elasticsearch

##### Write the Elasticsearch Sink Connector in a JSON file
```
cat > edgar-logs-es-sink.json
{
  "name": "edgar-logs-es-sink",
  "config": {
    "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
    "connection.url": "http://elasticsearch01:9200",
    "topics": "edgar-logs-formatted",
    "tasks.max": "10",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://schemaregistry:8081",
    "value.converter.schemas.enable": "true",
    "type.name": "_doc",
    "batch.size": 2000,
    "max.in.flight.requests": 5,
    "max.buffered.records": 20000,
    "linger.ms": 10,
    "flush.timeout.ms": 180000,
    "max.retries": 5,
    "retry.backoff.ms": 100,
    "connection.compression": false,
    "max.connection.idle.time.ms": 60000,
    "connection.timeout.ms": 1000,
    "read.timeout.ms": 3000,
    "key.ignore": true,
    "schema.ignore": false,
    "compact.map.entries": true,
    "topic.schema.ignore": "edgar-logs-formatted",
    "drop.invalid.message": true,
    "behavior.on.null.values": "IGNORE",
    "behavior.on.malformed.documents": "WARN",
    "write.method": "upsert",
    "transforms": "RenameField",
    "transforms.RenameField.type": "org.apache.kafka.connect.transforms.ReplaceField$Value",
    "transforms.RenameField.renames": "IP_ADDRESS:ip_address,REQUEST_DATE:request_date,REQUEST_TIME:request_time,REQUESTED_HOUR:requested_hour,ZONE:zone,CIK:cik,ACCESSION:accession,EXTENTION:extention,RESPONSE_CODE:response_code,RESPONSE_BYTES_SIZE:response_bytes_size,IDX:idx,NOREFER:norefer,NOAGENT:noagent,FIND:find,CRAWLER:crawler,BROWSER:browser"
  }
}
```

##### Launch the Elasticsearch Sink Connector
```
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d @edgar-logs-es-sink.json http://localhost:8083/connectors
```

##### To check the status of the Connector

- List of connectors - http://localhost:8083/connectors
- Status of the Connector - http://localhost:8083/connectors/edgar-logs-es-sink
- Status of the tasks of the Connector - http://localhost:8083/connectors/edgar-logs-es-sink/status


#### HDFS2 Sink Connector to load data from Kafka to HDFS

##### Write the HDFS2 Sink Connector in a JSON file
```
cat > edgar-hdfs-orc-sink.json
{
  "name": "edgar-hdfs-orc-sink",
  "config": {
    "connector.class": "io.confluent.connect.hdfs.HdfsSinkConnector",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://schemaregistry:8081",
    "value.converter.schema.enable": "true",
    "tasks.max": "10",
    "topics": "edgar-logs-formatted",
    "store.url": "hdfs://namenode:9000",
    "logs.dir": "/user/hive/warehouse/edgar_logs_orc/",
    "directory.delim": "/",
    "format.class": "io.confluent.connect.hdfs.orc.OrcFormat",
    "flush.size": "10",
    "rotate.interval.ms": "10000",
    "transforms": "RenameField",
    "transforms.RenameField.type": "org.apache.kafka.connect.transforms.ReplaceField$Value",
    "transforms.RenameField.renames": "IP_ADDRESS:ip_address,REQUEST_DATE:request_date,REQUEST_TIME:request_time,REQUESTED_HOUR:requested_hour,ZONE:zone,CIK:cik,ACCESSION:accession,RESPONSE_CODE:response_code,RESPONSE_BYTES_SIZE:response_bytes_size,IDX:idx,NOREFER:norefer,NOAGENT:noagent,FIND:find,CRAWLER:crawler,BROWSER:browser",
    "partitioner.class": "io.confluent.connect.storage.partitioner.FieldPartitioner",
    "partition.field.name": "request_date,requested_hour"
  }
}
```

##### Launch the HDFS2 Sink Connector
```
curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d @edgar-hdfs-orc-sink.json http://localhost:8083/connectors
```

##### To check the status of the Connector

- List of connectors - http://localhost:8083/connectors
- Status of the Connector - http://localhost:8083/connectors/edgar-hdfs-orc-sink.json
- Status of the tasks of the Connector - http://localhost:8083/connectors/edgar-hdfs-orc-sink.json/status

##### To delete a connector 

```
curl -X DELETE http://localhost:8083/connectors/<connector-name>
```

### Validate data in Elasticsearch, PostgreSQL & HDFS

#### Validating data in PostgreSQL

##### Login to PostgreSQL docker container

```
docker exec -it postgres sh
```

##### Login to PostgreSQL Shell

```
# psql -U hadoop -h postgres kafkadb
```

##### To List the databases

```
# \l
```

##### To connect to the particular database

```
# \c kafkadb
```

##### To list the tables in the database

```
# \d
```

##### To read data from the table

```
# select * from "edgar-logs-formatted";
```

#### Validating data in Elasticsearch

##### Open the below Kibana URL in the brower

- Kibana URL - http://localhost:5601
- Go to Dev Tools - http://localhost:5601/app/dev_tools#/console
- List indices - GET _cat/indices
- Read data from the index - GET edgar-logs-formatted/_search
- To count the number of documents - GET edgar-logs-formatted/_count

#### Validating data in HDFS

##### Open Namenode URL in the brower

- http://localhost:50070
- Utilities -> Browse File System
- Navigate to the hdfs path defined in HDFS Sink.