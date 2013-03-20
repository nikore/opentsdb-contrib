/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.opentsdb.kafka.consumer;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import kafka.consumer.*;
import kafka.consumer.Consumer;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.Message;
import net.opentsdb.client.netty.TsdbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Kafka Consumer pulls the data out of kafka and sends it to the OpenTSDB daemon
 */
@Singleton
public class TsdbConsumer extends AbstractIdleService {
  private static final String KAFKA_TOPIC = "tsdb.kafka.topic";

  private static final Logger logger = LoggerFactory.getLogger(TsdbConsumer.class);

  private static final Charset charset = Charset.forName("UTF-8");
  private static final CharsetDecoder decoder = charset.newDecoder();

  private final ConsumerConnector consumerConnector;
  private final TsdbClient client;
  private final String topic;

  @Inject
  public TsdbConsumer(TsdbClient client, Properties properties, @Named(KAFKA_TOPIC) String topic) {
    this.client = client;
    this.topic = topic;

    ConsumerConfig config = new ConsumerConfig(properties);

    this.consumerConnector = Consumer.createJavaConsumerConnector(config);
  }

  @Override
  protected void startUp() throws Exception {
    logger.info("Starting up TSDB Kafka Consumer");
    Map<String, Integer> topicCountMap = new HashMap<>();
    topicCountMap.put(topic, 1);
    Map<String, List<KafkaStream<Message>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
    KafkaStream<Message> stream =  consumerMap.get(topic).get(0);
    ConsumerIterator<Message> it = stream.iterator();
    while(it.hasNext()) {
      Message message = it.next().message();
      ByteBuffer buffer = message.payload();
      byte[] bytes = new byte[buffer.remaining()];
      buffer.get(bytes);
      String messageString = new String(bytes);
      logger.debug("Message is: {}", messageString);
      client.send(messageString);
    }
  }

  @Override
  protected void shutDown() throws Exception {
    logger.info("Shutting down TSDB Kafka Consumer");
    consumerConnector.shutdown();
  }
}
