package net.opentsdb.proxy.clients

import com.google.inject.Inject
import java.util.Properties
import org.slf4j.LoggerFactory
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import com.google.inject.name.Named

class KafkaProducer @Inject() (props: Properties, @Named("tsdb.kafka.topic") topicId: String) extends Client{
  private final val logger = LoggerFactory.getLogger(classOf[KafkaProducer])
  private final val producer: Producer[String, String] = new Producer(new ProducerConfig(props))

  def sendMessage(message: String) {
    logger.debug("Sending: {}", message)

    producer.send(new KeyedMessage[String, String](topicId, message))
  }
}
