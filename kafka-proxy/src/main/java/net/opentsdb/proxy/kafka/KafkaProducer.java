package net.opentsdb.proxy.kafka;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import kafka.javaapi.producer.Producer;
import kafka.javaapi.producer.ProducerData;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Sends the "put" commands that netty receives through kafka
 */
public class KafkaProducer {
  private static final String KAFKA_TOPIC_KEY = "tsdb.kafka.topic";
  private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
  private final Producer<String, String> producer;
  private final String topicId;

  /**
   * @param props   Properties file loaded from command line arg (needed to build a ProducerConfig)
   * @param topicId Topic ID from the config file
   */
  @Inject
  public KafkaProducer(Properties props, @Named(KAFKA_TOPIC_KEY) String topicId) {
    logger.info("Kafka topic: {}", topicId);

    this.topicId = topicId;
    this.producer = new Producer<>(new ProducerConfig(props));
  }

  /**
   * Sends the actual message
   *
   * @param message String[] that netty received that started with put
   */
  public void sendMessage(String[] message) {

    // turn the String[] in to a single string
    StringBuilder result = new StringBuilder();
    for (String aMessage : message) {
      result.append(aMessage).append(" ");
    }

    ProducerData<String, String> data = new ProducerData<>(topicId, result.toString());

    logger.debug("Sending: {}", result.toString());

    producer.send(data);
  }
}
