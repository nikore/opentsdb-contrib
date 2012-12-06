package net.opentsdb.proxy.netty.rpc;

import net.opentsdb.proxy.kafka.KafkaProducer;
import org.jboss.netty.channel.Channel;

public class PutDataPointRpc implements TelnetRpc {
  private final KafkaProducer producer;

  public PutDataPointRpc(KafkaProducer producer) {
    this.producer = producer;
  }

  @Override
  public void execute(Channel chan, String[] command) {
    if (command.length < 5) {
      throw new IllegalArgumentException("not enough arguments (need least 4, got " + (command.length) + ')');
    }
    final String metric = command[1];
    if (metric.length() <= 0) {
      throw new IllegalArgumentException("empty metric name");
    }

    final String value = command[3];
    if (value.length() <= 0) {
      throw new IllegalArgumentException("empty value");
    }

    producer.sendMessage(command);
  }
}
