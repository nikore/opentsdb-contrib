package net.opentsdb.kafka.consumer

import com.google.inject.Inject
import com.google.common.util.concurrent.AbstractIdleService
import java.util.Properties
import com.google.inject.name.Named
import org.slf4j.LoggerFactory
import kafka.consumer.{KafkaStream, ConsumerConfig, Consumer}
import net.opentsdb.client.netty.TsdbClient
import java.util.concurrent.Executors

class TsdbConsumer @Inject() (props: Properties, client: TsdbClient, @Named("tsdb.kafka.topic") topicName: String, @Named("tsdb.kafka.topic.partitions") partitions: Int) extends AbstractIdleService{
  private final val logger = LoggerFactory.getLogger(classOf[TsdbConsumer])
  private final val consumerConnector = Consumer.create(new ConsumerConfig(props))
  private final val executorPool = Executors.newFixedThreadPool(partitions, new ConsumerThreadFactory)


  def startUp() {
    logger.info("Starting up TSDB Kafka Consumer")
    val topicMessageStreams = consumerConnector.createMessageStreams(Predef.Map(topicName -> partitions))
    val topicStreams: List[KafkaStream[Array[Byte], Array[Byte]]] = topicMessageStreams.get(topicName).get

    for(stream <- topicStreams) {
      executorPool.execute(new Runnable() {
        override def run() {
          val it = stream.iterator()
          while(it.hasNext) {
            val messageString = new String(it.next().message)
            logger.debug("Message is: {}", messageString)
            client.send(messageString)
          }
        }
      })
    }
  }

  def shutDown() {
    logger.info("Shutting down TSDB Kafka Consumer")
    executorPool.shutdown()
    consumerConnector.shutdown()
  }
}
