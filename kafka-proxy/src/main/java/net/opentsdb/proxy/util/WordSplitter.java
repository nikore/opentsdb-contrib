package net.opentsdb.proxy.util;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.nio.charset.Charset;

public class WordSplitter extends OneToOneDecoder {
  private static final Charset CHARSET = Charset.forName("ISO-8859-1");

  public WordSplitter() {
  }

  @Override
  protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
    return ((ChannelBuffer) msg).toString(CHARSET).split(" ");
  }
}
