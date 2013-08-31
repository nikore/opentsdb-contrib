package net.opentsdb.client.netty

import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}
import org.jboss.netty.handler.codec.frame.{DelimiterBasedFrameDecoder, Delimiters}
import org.jboss.netty.handler.codec.string.{StringDecoder, StringEncoder}

object TsdbClientPipelineFactory {
  final val ENCODER = new StringEncoder()
  final val DECODER = new StringDecoder()
}

class TsdbClientPipelineFactory extends ChannelPipelineFactory  {
  val connmgr = new ConnectionManager
  val clientHandler = new TsdbClientHandler

  def getPipeline: ChannelPipeline = {
    val pipeline = Channels.pipeline()

    pipeline.addLast("connmgr", connmgr)
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, Delimiters.lineDelimiter: _*))
    pipeline.addLast("decoder", TsdbClientPipelineFactory.DECODER)
    pipeline.addLast("encoder", TsdbClientPipelineFactory.ENCODER)

    pipeline.addLast("handler", clientHandler)

    pipeline
  }
}
