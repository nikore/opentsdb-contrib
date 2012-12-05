package net.opentsdb.proxy.netty;

import org.jboss.netty.channel.Channel;

public interface TelnetRpc {

  void execute(Channel chan, String[] command);
}
