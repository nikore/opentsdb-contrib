package net.opentsdb.client.netty

import com.google.common.util.concurrent.AbstractIdleService
import org.slf4j.{Logger, LoggerFactory}
import com.google.inject.Inject
import org.jboss.netty.bootstrap.ClientBootstrap
import java.net.InetSocketAddress
import org.jboss.netty.channel.{Channel, ChannelFuture, ChannelFactory}

class TsdbClient @Inject() (factory: ChannelFactory, address: InetSocketAddress, pipelineFactory: TsdbClientPipelineFactory) extends AbstractIdleService {
  private final val logger: Logger = LoggerFactory.getLogger(classOf[TsdbClient])

  val bootstrap: ClientBootstrap = new ClientBootstrap(factory)
  bootstrap.setPipelineFactory(pipelineFactory)
  var channel: Channel = null
  private var lastWriteFuture: ChannelFuture = null

  protected def startUp() {
    val future: ChannelFuture = bootstrap.connect(address)
    channel = future.awaitUninterruptibly.getChannel
    if (!future.isSuccess) {
      future.getCause.printStackTrace()
      bootstrap.releaseExternalResources()
      System.exit(1)
    }
    logger.info("Done starting up TSDB client")
  }

  protected def shutDown() {
    logger.info("Shutting down TSDB client")
    if (lastWriteFuture != null) {
      lastWriteFuture.awaitUninterruptibly
    }
    channel.close.awaitUninterruptibly
    bootstrap.releaseExternalResources()
  }

  def send(message: String) {
    logger.debug("sending message {}", message)
    lastWriteFuture = channel.write(message + "\r\n")
  }
}

