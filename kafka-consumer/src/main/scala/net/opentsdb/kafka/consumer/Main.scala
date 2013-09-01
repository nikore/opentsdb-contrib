package net.opentsdb.kafka.consumer

import org.slf4j.LoggerFactory
import java.io.{FileInputStream, BufferedInputStream, File}
import java.util.Properties
import com.google.inject.Guice
import net.opentsdb.kafka.consumer.modules.ConsumerModule
import net.opentsdb.client.netty.modules.TsdbClientModule
import net.opentsdb.client.netty.TsdbClient

class Main {}

object Main {
  private final val logger = LoggerFactory.getLogger(classOf[Main])

  def main(args: Array[String]) {
    val props = loadProps(new File(args{0}))

    val injector = Guice.createInjector(new ConsumerModule(props), new TsdbClientModule(props))

    logger.info("Starting TSDB Client...")
    val client = injector.getInstance(classOf[TsdbClient])
    client.startAndWait()

    logger.info("Starting TSDB Consumer...")
    val consumer = injector.getInstance(classOf[TsdbConsumer])
    consumer.startAndWait()

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        logger.info("Shutting down consumer")
        consumer.stopAndWait()
        client.stopAndWait()
      }
    })
  }

  private def loadProps(config: File): Properties = {
    if(!config.canRead) {
      System.err.println("Cannot open config file: " + config)
      System.exit(1)
    }

    val props = new Properties()
    props.load(new BufferedInputStream(new FileInputStream(config)))

    logger.info("Loadded the file properties {}", props.toString)

    props
  }
}
