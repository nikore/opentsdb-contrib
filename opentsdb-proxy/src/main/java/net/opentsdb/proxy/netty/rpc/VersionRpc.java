package net.opentsdb.proxy.netty.rpc;

import org.jboss.netty.channel.Channel;

public class VersionRpc implements TelnetRpc {
  @Override
  public void execute(Channel chan, String[] command) {
    if (chan.isConnected()) {
      chan.write("OpenTSDB To Kafka Proxy " + "\n" + "Version 1.0" + "\n");
    }
  }
}
