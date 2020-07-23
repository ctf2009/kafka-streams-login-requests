# Kafka Streams Demo - Login Requests
This is an example of using Kafa Streams to process Login Events. There are a number of Microservices which work to process, enrich and aggregate the events in order to gain insights

There are a number of components

- Login Streams Processor
- Ip Enrichment
- Sucpicious Ip Checking
- Known Locations Checking
- Viewer
- Drivers

## Getting Started

First you need to configure Kafka and Zookeeper. There is a `docker-compose` file in the `testing` directory which you can run to create a single node cluster and work with the demo

Following this, you need to create the topics as shown below. You may change the partition counts but please ensure you make them the same for all the topics. Having diffent numbers of partitions on topics that have joins performed on them can cause missed joins

### Create Topics
```
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic login_request;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic login_request_history;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic login_request_ip_processing;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic ip_enriched;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic user_known_locations --config cleanup.policy=compact —config delete.retention.ms=15552000000 --config segment.ms=86400000;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic notifications;
```

### Start all the Mircoservices
You can build the entire project simple by running `gradlew clean build`. Each Microservice produces its own Jar. You can then run the Jar as normal. Alternativly you can fire everything up in your favourite IDE

### Run the Drivers
There are a number of drivers that will publish messages to the Login Request topic. You should start to see messages appearing in the Viewer

## Other Useful Kafka Commands

### Get All Topics
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --list --bootstrap-server localhost:9092`

### Describe Topics
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic login_request`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic login_request_history`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic ip_processing`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic ip_enriched`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic user_known_locations`

### Get All Consumer Groups
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --list --bootstrap-server localhost:9092`

### Get Specifc Consumer Group
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group login-viewer-group --describe --bootstrap-server localhost:9092`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group login-processor-group --describe --bootstrap-server localhost:9092`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group ip-enrichment-group --describe --bootstrap-server localhost:9092`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group known-locations-group --describe --bootstrap-server localhost:9092`

### Console Consume From Topic
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic login_request --from-beginning`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic login_request_history --from-beginning`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic login_request_ip_processing --from-beginning`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic ip_enriched --from-beginning`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic user_known_locations --from-beginning`

`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic notifications --from-beginning`