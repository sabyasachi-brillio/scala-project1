salesforce Sync Service

## Running the example:

1. docker run -d -p 2181:2181 -p 9092:9092 --rm --env ADVERTISED_HOST=kafka --env ADVERTISED_PORT=9092 --name kafka -h kafka johnnypark/kafka-zookeeper
2. cd salesforce-sync-service
3. docker build -t "salesforce-sync-service:latest" ./docker/.
4. docker run -p 8080:8080 --rm -it --name=salesforce-sync-service --link kafka --volume=${HOME}/code/salesforce-sync-service/:/code/salesforce-sync-service salesforce-sync-service
5. cd /code/salesforce-sync-service
6. sbt test
7. sbt "runMain com.jemstep.producer.PlainSinkProducerMain"
8. sbt "runMain com.jemstep.Main"
9. sbt "runMain com.jemstep.oauth.OAuthSSLHandler"

## Connecting to another Kafka host

The examples connect to "kafka:9092" by default, which should be running on the started docker container from step 1.

Use the properties "kafka.host" and "kafka.port" in order to specify your own host and/or port, for example:

```bash
$ sbt -Dkafka.host=127.0.0.1 -Dkafka.port=9092 "runMain com.jemstep.producer.PlainSinkProducerMain"
$ sbt -Dkafka.host=127.0.0.1 -Dkafka.port=9092 "runMain com.jemstep.Main"
```