package net.opentsdb.client.netty

import org.jboss.netty.channel._
import org.slf4j.{LoggerFactory, Logger}

class TsdbClientHandler extends SimpleChannelUpstreamHandler {
  private final val logger: Logger = LoggerFactory.getLogger(classOf[TsdbClient])



  override def handleUpstream(ctx: ChannelHandlerContext, e: ChannelEvent) = {
    e match {
      case e: ChannelStateEvent => logger.info(e.toString)
      case _ =>
    }

    super.handleUpstream(ctx, e)
  }

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) = {
    logger.error(e.getMessage.toString)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) = {
    logger.warn("Unexpected exception from downstream: {}", e.getCause)
    e.getChannel.close()
  }
}
