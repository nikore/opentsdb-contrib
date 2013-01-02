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

package net.opentsdb.client.netty.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.opentsdb.client.netty.TsdbClientPipelineFactory;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

public class TsdbClientModule extends AbstractModule {
  private final static String TSDB_PORT = "tsdb.port";
  private final static String TSDB_HOST = "tsdb.host";

  private final Properties properties;

  public TsdbClientModule(Properties properties) {
    this.properties = properties;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);
    bind(TsdbClientPipelineFactory.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  InetSocketAddress provideSocketAddress(@Named(TSDB_HOST) String host, @Named(TSDB_PORT) Integer port) {
    return new InetSocketAddress(host, port);
  }

  @Provides
  @Singleton
  ChannelFactory provideChannelFactory() {
    return new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
  }
}
