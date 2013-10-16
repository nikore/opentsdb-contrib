package net.opentsdb.kafka.consumer

import com.google.inject.Inject
import com.google.common.util.concurrent.AbstractIdleService
import java.util.Properties
import com.google.inject.name.Named
import org.slf4j.LoggerFactory
import kafka.consumer.{ConsumerConfig, Consumer}
import java.util.concurrent.Executors
import javax.inject.Singleton
import kafka.serializer.{DefaultDecoder, StringDecoder}
import net.opentsdb.core.{Tags, TSDB}
import net.opentsdb.stats.StatsCollector
import java.util

@Singleton
class TsdbConsumer @Inject() (props: Properties, tsdbClient: TSDB, @Named("tsdb.kafka.topic") topicName: String,
                              @Named("tsdb.kafka.topic.partitions") partitions: Int) extends AbstractIdleService{
  private final val logger = LoggerFactory.getLogger(classOf[TsdbConsumer])
  private final val consumerConnector = Consumer.create(new ConsumerConfig(props))
  private final val executorPool = Executors.newFixedThreadPool(partitions, new ConsumerThreadFactory)


  def startUp() {

    tsdbClient.collectStats(new StatsCollector("tsd") {
      def emit(line: String) = logger.info(line)
    })

    logger.info("Starting up TSDB Kafka Consumer")
    val topicMessageStreams = consumerConnector.createMessageStreams(Predef.Map(topicName -> partitions), new DefaultDecoder,
      new StringDecoder())
    val topicStreams = topicMessageStreams.get(topicName).get

    for(stream <- topicStreams) {
      executorPool.execute(new Runnable() {
        override def run() {
          for(kafkaMessage <- stream) {
            val words = Tags.splitString(kafkaMessage.message, ' ')

            val metric = words{1}
            val timestamp = Tags.parseLong(words{2})
            val value = words{3}

            val tags: util.HashMap[String, String] = new util.HashMap[String, String]

            for (i <- 4 until words.length if !words(i).isEmpty) {
              Tags.parse(tags, words(i))
            }

            if (value.equals("NaN") || value.startsWith("N")) {
              tsdbClient.addPoint(metric, timestamp, 0, tags)
            } else if (Tags.looksLikeInteger(value)) {
              tsdbClient.addPoint(metric, timestamp, Tags.parseLong(value), tags)
            } else {
              tsdbClient.addPoint(metric, timestamp, java.lang.Float.parseFloat(value), tags)
            }
          }
        }
      })
    }
  }

  def shutDown() {
    logger.info("Shutting down TSDB Kafka Consumer")
    tsdbClient.shutdown().joinUninterruptibly()
    executorPool.shutdown()
    consumerConnector.shutdown()
  }
}


