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

import net.opentsdb.proxy.clients.Client;
import net.opentsdb.proxy.netty.rpc.PutDataPointRpc;
import net.opentsdb.proxy.netty.rpc.TelnetRpc;
import net.opentsdb.proxy.netty.rpc.VersionRpc;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RpcHandler extends SimpleChannelUpstreamHandler {
  private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);
  private final Client client;

  public RpcHandler(Client client) {
    this.client = client;
  }

  @Override
  public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent msgevent) {
    final Object message = msgevent.getMessage();
    if (message instanceof String[]) {
      handleTelnetRpc(msgevent.getChannel(), (String[]) message);
    } else {
      logger.error(msgevent.getChannel() + "Unexpected message type " + message.getClass() + ": " + message);
    }
  }

  private void handleTelnetRpc(final Channel channel, final String[] command) {
    TelnetRpc rpc;
    logger.debug("Command is {}", Arrays.toString(command));
    switch (command[0]) {
      case "put":
        rpc = new PutDataPointRpc(client);
        break;
      case "version":
        rpc = new VersionRpc();
        break;
      default:
        rpc = null;
    }
    if (rpc == null) {
      logger.error("unknown command " + command[0]);
    } else {
      rpc.execute(channel, command);
    }
  }
}
