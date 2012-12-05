Opentsdb services for using kafka to transit the data

Kafka-proxy: Receives the tsdb requests via the telnet socket and produces a kafka message

opentsdb-consumer: Takes kafka messages and sends them to the OpenTSDB daemon (telnet socket)