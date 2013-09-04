package net.opentsdb.kafka.consumer

import java.util.concurrent.ThreadFactory

class ConsumerThreadFactory extends ThreadFactory {
  var counter: Int = 0
  val prefix = "ConsumerThread - "

  def newThread(r: Runnable): Thread = {
    counter = counter + 1
    new Thread(r, prefix + counter)
  }
}
