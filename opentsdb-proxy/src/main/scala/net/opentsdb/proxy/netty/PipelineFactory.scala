package net.opentsdb.proxy.netty

import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}
import com.google.inject.Inject
import net.opentsdb.proxy.clients.Client
import org.jboss.netty.handler.codec.string.StringEncoder
import net.opentsdb.proxy.util.WordSplitter

object PipelineFactory {
  val ENCODER: StringEncoder = new StringEncoder
  val DECODER: WordSplitter = new WordSplitter
}

class PipelineFactory @Inject() (client: Client) extends ChannelPipelineFactory {
  private final val rpchandler = new RpcHandler(client)
  private final val connmgr = new ConnectionManager

  def getPipeline: ChannelPipeline = {
    val pipeline = Channels.pipeline()

    pipeline.addLast("connmgr", connmgr)
    pipeline.addLast("framer", new LineBasedFrameDecoder(1024))
    pipeline.addLast("encoder", PipelineFactory.ENCODER)
    pipeline.addLast("decoder", PipelineFactory.DECODER)
    pipeline.addLast("handler", rpchandler)

    pipeline
  }
}