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

package net.opentsdb.proxy.netty.rpc;

import net.opentsdb.proxy.clients.Client;
import org.jboss.netty.channel.Channel;

public class PutDataPointRpc implements TelnetRpc {
  private final Client client;

  public PutDataPointRpc(Client client) {
    this.client = client;
  }

  @Override
  public void execute(Channel chan, String[] commands) {
    if (commands.length < 5) {
      throw new IllegalArgumentException("not enough arguments (need least 4, got " + (commands.length) + ')');
    }
    final String metric = commands[1];
    if (metric.length() <= 0) {
      throw new IllegalArgumentException("empty metric name");
    }

    final String value = commands[3];
    if (value.length() <= 0) {
      throw new IllegalArgumentException("empty value");
    }

    StringBuilder sb = new StringBuilder();
    for (String command : commands) {
      sb.append(command).append(" ");
    }

    client.sendMessage(sb.toString());
  }
}
