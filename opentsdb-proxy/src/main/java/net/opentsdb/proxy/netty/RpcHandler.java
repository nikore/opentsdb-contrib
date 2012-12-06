package net.opentsdb.proxy.netty;

import net.opentsdb.proxy.kafka.KafkaProducer;
import net.opentsdb.proxy.netty.rpc.PutDataPointRpc;
import net.opentsdb.proxy.netty.rpc.TelnetRpc;
import net.opentsdb.proxy.netty.rpc.VersionRpc;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RpcHandler extends SimpleChannelUpstreamHandler {
  private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);
  private final KafkaProducer producer;

  public RpcHandler(KafkaProducer producer) {
    this.producer = producer;
  }

  @Override
  public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent msgevent) {
    final Object message = msgevent.getMessage();
    if (message instanceof String[]) {
      handleTelnetRpc(msgevent.getChannel(), (String[]) message);
    } else {
      logger.error(msgevent.getChannel() + "Unexpected message type " + message.getClass() + ": " + message);
    }
  }

  private void handleTelnetRpc(final Channel channel, final String[] command) {
    TelnetRpc rpc;
    logger.debug("Command is {}", Arrays.toString(command));
    switch (command[0]) {
      case "put":
        rpc = new PutDataPointRpc(producer);
        break;
      case "version":
        rpc = new VersionRpc();
        break;
      default:
        rpc = null;
    }
    if (rpc == null) {
      logger.error("unknown command " + command[0]);
    } else {
      rpc.execute(channel, command);
    }
  }

}
