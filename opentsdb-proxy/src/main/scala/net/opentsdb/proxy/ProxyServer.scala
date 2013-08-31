package net.opentsdb.proxy

import org.slf4j.LoggerFactory
import java.io.{FileInputStream, BufferedInputStream, File}
import java.util.Properties
import com.google.inject.Guice
import net.opentsdb.proxy.modules.ProxyModule
import net.opentsdb.proxy.netty.NettyServer

class ProxyServer{}

object ProxyServer {
  private final val logger = LoggerFactory.getLogger(classOf[ProxyServer])

  def main(args: Array[String]) {
    val config = new File(args{0})
    if(!config.canRead) {
      System.err.println("Cannot open config file: " + config)
      System.exit(1)
    }

    val props = new Properties()
    props.load(new BufferedInputStream(new FileInputStream(config)))

    logger.info("Loadded the file properties {}", props.toString)

    val injector = Guice.createInjector(new ProxyModule(props))

    val server = injector.getInstance(classOf[NettyServer])

    logger.info("Starting Server ...")

    server.startAndWait()

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() = {
        logger.info("shutting down server")
        server.stopAndWait()
      }
    })
  }
}
