package net.opentsdb.proxy.netty.rpc;

import org.jboss.netty.channel.Channel;

public interface TelnetRpc {

  void execute(Channel chan, String[] command);
}
