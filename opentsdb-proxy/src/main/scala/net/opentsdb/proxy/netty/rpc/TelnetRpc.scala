package net.opentsdb.proxy.netty.rpc

import org.jboss.netty.channel.Channel

trait TelnetRpc {
  def execute(chan: Channel, command: Array[String])
}
