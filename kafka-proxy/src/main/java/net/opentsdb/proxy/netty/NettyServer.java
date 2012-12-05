package net.opentsdb.proxy.netty;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class NettyServer extends AbstractIdleService {
  private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);
  private final ChannelGroup allChannels;
  private final SocketAddress address;
  private final ChannelFactory factory;
  private final ServerBootstrap bootstrap;
  private final PipelineFactory pipelineFactory;

  @Inject
  NettyServer(ChannelFactory factory, ChannelGroup allChannels, SocketAddress address, PipelineFactory pipelineFactory) {
    this.factory = factory;
    this.bootstrap = new ServerBootstrap(factory);
    this.allChannels = allChannels;
    this.address = address;
    this.pipelineFactory = pipelineFactory;
  }

  @Override
  protected void startUp() throws Exception {
    bootstrap.setPipelineFactory(pipelineFactory);
    bootstrap.setOption("child.tcpNoDelay", true);
    bootstrap.setOption("child.keepAlive", true);
    bootstrap.setOption("reuseAddress", true);

    logger.info("Server started");
    bootstrap.bind(address);
  }

  @Override
  protected void shutDown() throws Exception {
    allChannels.close().awaitUninterruptibly();
    factory.releaseExternalResources();
  }
}
