package net.opentsdb.kafka.consumer

import com.google.inject.Inject
import com.google.common.util.concurrent.AbstractIdleService
import java.util.Properties
import com.google.inject.name.Named
import org.slf4j.LoggerFactory
import kafka.consumer.{ConsumerIterator, ConsumerConfig, Consumer}
import net.opentsdb.client.netty.TsdbClient
import scala.collection.immutable.HashMap
import kafka.message.Message
import java.nio.ByteBuffer

class TsdbConsumer @Inject() (props: Properties, client: TsdbClient ,@Named("tsdb.kafka.topic") topicName: String) extends AbstractIdleService{
  private final val logger = LoggerFactory.getLogger(classOf[TsdbConsumer])
  private final val consumerConnector = Consumer.create(new ConsumerConfig(props))

  def startUp() {
    logger.info("Starting up TSDB Kafka Consumer")
    val topicCountMap = HashMap(topicName -> 1)

    val consumerMap = consumerConnector.createMessageStreams(topicCountMap)
    val stream = consumerMap.get(topicName).get(0)
    val it: ConsumerIterator[Message] = stream.iterator()

    while(it.hasNext()) {
      val message = it.next().message
      val buffer: ByteBuffer = message.payload
      val bytes = new Array[Byte](buffer.remaining())
      buffer.get(bytes)
      val messageString = new String(bytes)
      logger.debug("Message is: {}", messageString)
      client.send(messageString)
    }
  }

  def shutDown() {
    logger.info("Shutting down TSDB Kafka Consumer")
    consumerConnector.shutdown()
  }
}
