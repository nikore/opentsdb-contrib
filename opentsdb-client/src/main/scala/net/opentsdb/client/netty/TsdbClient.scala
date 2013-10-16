package net.opentsdb.client.netty

import com.google.common.util.concurrent.AbstractIdleService
import org.slf4j.{Logger, LoggerFactory}
import com.google.inject.Inject
import org.jboss.netty.bootstrap.ClientBootstrap
import java.net.InetSocketAddress
import org.jboss.netty.channel.{ChannelFutureListener, ChannelFuture, ChannelFactory}
import javax.inject.Singleton

@Singleton
class TsdbClient @Inject() (factory: ChannelFactory, address: InetSocketAddress, pipelineFactory: TsdbClientPipelineFactory) extends AbstractIdleService {
  private final val logger: Logger = LoggerFactory.getLogger(classOf[TsdbClient])

  val bootstrap: ClientBootstrap = new ClientBootstrap(factory)
  bootstrap.setPipelineFactory(pipelineFactory)
  bootstrap.setOption("tcpNoDelay", true)
  bootstrap.setOption("sendBufferSize", 1048576)
  bootstrap.setOption("receiveBufferSize", 1048576)
  bootstrap.setOption("writeBufferHighWaterMark", 10*64*1024)
  val future: ChannelFuture = bootstrap.connect(address)

  protected def startUp() {
//    if (!future.isSuccess) {
//      future.getCause.printStackTrace()
//      bootstrap.releaseExternalResources()
//      System.exit(1)
//    }
    logger.info("Done starting up TSDB client")
  }

  protected def shutDown() {
    logger.info("Shutting down TSDB client")
//    if (lastWriteFuture != null) {
//      lastWriteFuture.awaitUninterruptibly
//    }
//    channel.close.awaitUninterruptibly
    bootstrap.releaseExternalResources()
  }

  def send(message: String) {
//    logger.debug("sending message {}", message)
    val channelFuture = future.getChannel.write(new StringBuilder(message).append("\r\n").toString())
    channelFuture.addListener(new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) {
        if(future.isDone) {
          if(!future.isSuccess) {
            future.getChannel.close()
          } else {

          }
        }
      }
    })
  }
}

