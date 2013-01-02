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

package net.opentsdb.proxy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.opentsdb.proxy.modules.ProxyModule;
import net.opentsdb.proxy.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Main method controls the life cycle of the app
 */
public class ProxyServer {
  private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);

  public static void main(String... args) throws Exception {

    File config = new File(args[0]);
    if (!config.canRead()) {
      System.err.println("Cannot open config file: " + config);
      System.exit(1);
    }

    Properties props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(config)));

    logger.info("Loaded the file properties: {}", props.toString());

    Injector injector = Guice.createInjector(new ProxyModule(props));

    final NettyServer server = injector.getInstance(NettyServer.class);

    logger.info("Starting Server ...");
    server.startAndWait();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        logger.info("Shutting down server");
        server.stopAndWait();
      }
    });
  }
}
