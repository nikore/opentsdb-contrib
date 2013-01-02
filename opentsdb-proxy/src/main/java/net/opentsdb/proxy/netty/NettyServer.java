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

package net.opentsdb.proxy.netty;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class NettyServer extends AbstractIdleService {
  private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);
  private final ChannelGroup allChannels;
  private final SocketAddress address;
  private final ChannelFactory factory;
  private final ServerBootstrap bootstrap;
  private final PipelineFactory pipelineFactory;

  @Inject
  NettyServer(ChannelFactory factory, ChannelGroup allChannels, SocketAddress address, PipelineFactory pipelineFactory) {
    this.factory = factory;
    this.bootstrap = new ServerBootstrap(factory);
    this.allChannels = allChannels;
    this.address = address;
    this.pipelineFactory = pipelineFactory;
  }

  @Override
  protected void startUp() throws Exception {
    bootstrap.setPipelineFactory(pipelineFactory);
    bootstrap.setOption("child.tcpNoDelay", true);
    bootstrap.setOption("child.keepAlive", true);
    bootstrap.setOption("reuseAddress", true);

    logger.info("Server started on {}", address.toString());
    bootstrap.bind(address);
  }

  @Override
  protected void shutDown() throws Exception {
    allChannels.close().awaitUninterruptibly();
    factory.releaseExternalResources();
  }
}
