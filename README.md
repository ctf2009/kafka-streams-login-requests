Create Topics
```
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic login_request;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic login_request_history;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic login_request_ip_processing;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic ip_enriched;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic user_known_locations --config cleanup.policy=compact —config delete.retention.ms=15552000000 --config segment.ms=86400000;
docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4 --topic notifications;
```

Get All topics
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --list --bootstrap-server localhost:9092`

Describe topics
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic login_request`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic login_request_history`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic ip_processing`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic ip_enriched`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-topics --describe --bootstrap-server localhost:9092 --topic user_known_locations`

Get All Consumer Groups
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --list --bootstrap-server localhost:9092`

Get Status of a Consumer Group
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group login-viewer-group --describe --bootstrap-server localhost:9092`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group login-processor-group --describe --bootstrap-server localhost:9092`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group ip-enrichment-group --describe --bootstrap-server localhost:9092`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-consumer-groups --group known-locations-group --describe --bootstrap-server localhost:9092`

Consume from Topic
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic login_request --from-beginning`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic login_request_history --from-beginning`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic ip_processing --from-beginning`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic ip_enriched --from-beginning`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic user_known_locations --from-beginning`
`docker run --net=host confluentinc/cp-server:5.4.0 /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic notifications --from-beginning`