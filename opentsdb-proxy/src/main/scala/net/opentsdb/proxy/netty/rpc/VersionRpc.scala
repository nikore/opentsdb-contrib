package net.opentsdb.proxy.netty.rpc

import org.jboss.netty.channel.Channel

class VersionRpc extends TelnetRpc{
  def execute(chan: Channel, command: Array[String]) {
    if (chan.isConnected) {
      chan.write("OpenTSDB To Kafka Proxy " + "\n" + "Version 1.0" + "\n");
    }
  }
}
