package net.opentsdb.kafka.consumer;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;

import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import kafka.message.MessageAndMetadata;
import kafka.serializer.DefaultDecoder;
import net.opentsdb.kafka.consumer.netty.TsdbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Properties;

@Singleton
public class TsdbConsumer extends AbstractIdleService{
  private static final String KAFKA_TOPIC = "tsdb.kafka.topic";

  private static final Logger logger = LoggerFactory.getLogger(TsdbConsumer.class);

  private static final Charset charset = Charset.forName("UTF-8");
  private static final CharsetDecoder decoder = charset.newDecoder();

  private final ConsumerConnector consumerConnector;
  private final TsdbClient client;
  private final String topic;

  @Inject
  public TsdbConsumer(TsdbClient client, Properties properties, @Named(KAFKA_TOPIC) String topic) {
    this.client = client;
    this.topic = topic;

    ConsumerConfig config = new ConsumerConfig(properties);

    this.consumerConnector = Consumer.createJavaConsumerConnector(config);
  }

  @Override
  protected void startUp() throws Exception {
    logger.info("Starting up TSDB Kafka Consumer");
    KafkaStream<Message> stream = consumerConnector.createMessageStreamsByFilter(new Whitelist(topic), 1, new DefaultDecoder()).get(0);
    for (MessageAndMetadata<Message> aStream : stream) {
      String message = decoder.decode(aStream.message().payload()).toString();
      logger.info("Message is: {}", message);
      client.send(message);
    }
  }

  @Override
  protected void shutDown() throws Exception {
    logger.info("Shutting down TSDB Kafka Consumer");
    consumerConnector.shutdown();
  }
}
