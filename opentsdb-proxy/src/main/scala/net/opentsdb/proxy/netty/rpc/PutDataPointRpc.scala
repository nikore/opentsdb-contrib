package net.opentsdb.proxy.netty.rpc

import org.jboss.netty.channel.Channel
import net.opentsdb.proxy.clients.Client

class PutDataPointRpc (client: Client) extends TelnetRpc{
  def execute(chan: Channel, command: Array[String]) {
    if(command.length < 5) throw new IllegalArgumentException("not enough arguments (need least 4, got " + command.length + ')')
    if(command{1}.length <= 0) throw new IllegalArgumentException("empty metric name")
    if(command{3}.length <= 0) throw new IllegalArgumentException("empty value")

    val sb = new StringBuilder

    for(cmd <- command) {
      sb.append(cmd).append(" ")
    }

    client.sendMessage(sb.toString())
  }
}
