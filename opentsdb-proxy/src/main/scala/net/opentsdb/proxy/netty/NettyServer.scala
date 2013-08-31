package net.opentsdb.proxy.netty

import com.google.common.util.concurrent.AbstractIdleService
import com.google.inject.Inject
import org.jboss.netty.channel.ChannelFactory
import org.jboss.netty.channel.group.ChannelGroup
import java.net.SocketAddress
import org.slf4j.LoggerFactory
import org.jboss.netty.bootstrap.ServerBootstrap

class NettyServer @Inject() (factory: ChannelFactory, allChannels: ChannelGroup, address: SocketAddress, pipelineFactory: PipelineFactory) extends AbstractIdleService {
  private final val logger = LoggerFactory.getLogger(classOf[NettyServer])
  private final val bootstrap = new ServerBootstrap(factory)

  def startUp() {
    bootstrap.setPipelineFactory(pipelineFactory)
    bootstrap.setOption("child.tcpNoDelay", true)
    bootstrap.setOption("child.keepAlive", true)
    bootstrap.setOption("reuseAddress", true)

    logger.info("Server started on {}", address.toString)
    bootstrap.bind(address)
  }

  def shutDown() {
    allChannels.close().awaitUninterruptibly()
    factory.releaseExternalResources()
  }
}
