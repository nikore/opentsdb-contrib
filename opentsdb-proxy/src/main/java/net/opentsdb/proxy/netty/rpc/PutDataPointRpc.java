package net.opentsdb.proxy.netty.rpc;

import net.opentsdb.proxy.clients.Client;
import org.jboss.netty.channel.Channel;

public class PutDataPointRpc implements TelnetRpc {
  private final Client client;

  public PutDataPointRpc(Client client) {
    this.client = client;
  }

  @Override
  public void execute(Channel chan, String[] commands) {
    if (commands.length < 5) {
      throw new IllegalArgumentException("not enough arguments (need least 4, got " + (commands.length) + ')');
    }
    final String metric = commands[1];
    if (metric.length() <= 0) {
      throw new IllegalArgumentException("empty metric name");
    }

    final String value = commands[3];
    if (value.length() <= 0) {
      throw new IllegalArgumentException("empty value");
    }

    StringBuilder sb = new StringBuilder();
    for (String command : commands) {
      sb.append(command).append(" ");
    }

    client.sendMessage(sb.toString());
  }
}
