package net.opentsdb.proxy.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClient implements Client {
  private final static Logger logger = LoggerFactory.getLogger(EchoClient.class);

  @Override
  public void sendMessage(String message) {
    logger.info("Got message: {}", message);
  }
}
