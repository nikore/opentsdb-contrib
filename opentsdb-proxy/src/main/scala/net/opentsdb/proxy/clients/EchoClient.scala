package net.opentsdb.proxy.clients

import org.slf4j.LoggerFactory

class EchoClient extends Client{
  private final val logger = LoggerFactory.getLogger(classOf[EchoClient])

  def sendMessage(message: String) {
    logger.info("Got message: {}", message)
  }
}
