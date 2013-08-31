package net.opentsdb.proxy.netty

import org.jboss.netty.channel.{Channel, MessageEvent, ChannelHandlerContext, SimpleChannelUpstreamHandler}
import org.slf4j.LoggerFactory
import net.opentsdb.proxy.clients.Client
import net.opentsdb.proxy.netty.rpc.{VersionRpc, PutDataPointRpc}

class RpcHandler (client: Client) extends SimpleChannelUpstreamHandler{
  private final val logger = LoggerFactory.getLogger(classOf[RpcHandler])

  override def messageReceived(ctx: ChannelHandlerContext, msgEvent: MessageEvent) = {
    val message = msgEvent.getMessage

    message match {
      case msg: Array[String] => handleTelnetRpc(msgEvent.getChannel, msg)
      case _ => logger.error(msgEvent.getChannel + "Unexpected message type " + message.getClass + ": " + message.toString)
    }
  }

  private def handleTelnetRpc(channel: Channel, command: Array[String]) = {
    logger.debug("Command is {}", command.toString)

    command{0} match {
      case "put" => {
        val rpc = new PutDataPointRpc(client)
        rpc.execute(channel,command)
      }
      case "version" => {
        val rpc = new VersionRpc()
        rpc.execute(channel, command)
      }
      case _ => {
        logger.error("unkown command " + command{0})
      }
    }
  }
}
