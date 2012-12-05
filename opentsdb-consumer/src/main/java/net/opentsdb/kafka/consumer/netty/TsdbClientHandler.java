package net.opentsdb.kafka.consumer.netty;

import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TsdbClientHandler extends SimpleChannelUpstreamHandler {
  private static final Logger logger = LoggerFactory.getLogger(TsdbClientHandler.class);

  @Override
  public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
    if (e instanceof ChannelStateEvent) {
      logger.info(e.toString());
    }

    super.handleUpstream(ctx, e);
  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    logger.error(e.getMessage().toString());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    logger.warn("Unexpected exception from downstream: {}", e.getCause());
    e.getChannel().close();
  }
}
