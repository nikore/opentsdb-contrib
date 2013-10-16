package net.opentsdb.client.netty

import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}
import org.jboss.netty.handler.codec.frame.{DelimiterBasedFrameDecoder, Delimiters}
import org.jboss.netty.handler.codec.string.{StringDecoder, StringEncoder}

object TsdbClientPipelineFactory {
  final val connmgr = new ConnectionManager
}

class TsdbClientPipelineFactory extends ChannelPipelineFactory  {
  def getPipeline: ChannelPipeline = {
    val pipeline = Channels.pipeline()

    pipeline.addLast("connmgr", TsdbClientPipelineFactory.connmgr)
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter: _*))
    pipeline.addLast("decoder", new StringDecoder())
    pipeline.addLast("encoder", new StringEncoder())
    pipeline.addLast("handler", new TsdbClientHandler)

    pipeline
  }
}
