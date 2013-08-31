package net.opentsdb.proxy.util

import org.jboss.netty.handler.codec.oneone.OneToOneDecoder
import org.jboss.netty.channel.{Channel, ChannelHandlerContext}
import java.nio.charset.Charset
import org.jboss.netty.buffer.ChannelBuffer


object WordSplitter {
  final val CHARSET = Charset.forName("ISO-8859-1")
}

class WordSplitter extends OneToOneDecoder {

  def decode(ctx: ChannelHandlerContext, channel: Channel, msg: scala.Any): AnyRef = {
    msg match {
      case matchMsg: ChannelBuffer => {
        matchMsg.toString(WordSplitter.CHARSET).split(" ")
      }
    }
  }
}
