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

package net.opentsdb.proxy.clients;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import kafka.javaapi.producer.Producer;
import kafka.javaapi.producer.ProducerData;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Sends the "put" commands that netty receives through kafka
 */
public class KafkaProducer implements Client {
  private static final String KAFKA_TOPIC_KEY = "tsdb.kafka.topic";
  private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
  private final Producer<String, String> producer;
  private final String topicId;

  /**
   * @param props   Properties file loaded from command line arg (needed to build a ProducerConfig)
   * @param topicId Topic ID from the config file
   */
  @Inject
  public KafkaProducer(Properties props, @Named(KAFKA_TOPIC_KEY) String topicId) {
    logger.info("Kafka topic: {}", topicId);

    this.topicId = topicId;
    this.producer = new Producer<>(new ProducerConfig(props));
  }

  /**
   * Sends the actual message
   *
   * @param message String[] that netty received that started with put
   */
  public void sendMessage(String message) {

    ProducerData<String, String> data = new ProducerData<>(topicId, message);

    logger.debug("Sending: {}", message);

    producer.send(data);
  }
}
