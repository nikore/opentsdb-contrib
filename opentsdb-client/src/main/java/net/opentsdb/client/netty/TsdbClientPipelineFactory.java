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

package net.opentsdb.client.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class TsdbClientPipelineFactory implements ChannelPipelineFactory {
  private static final ChannelBuffer[] DELIMITERS = Delimiters.lineDelimiter();
  private static final StringEncoder ENCODER = new StringEncoder();
  private static final StringDecoder DECODER = new StringDecoder();

  // Those are sharable but maintain some state, so a single instance per
  // PipelineFactory is needed.
  private final ConnectionManager connmgr = new ConnectionManager();
  private final TsdbClientHandler clientHandler = new TsdbClientHandler();

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    final ChannelPipeline pipeline = Channels.pipeline();

    pipeline.addLast("connmgr", connmgr);
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(1024, DELIMITERS));
    pipeline.addLast("decoder", DECODER);
    pipeline.addLast("encoder", ENCODER);

    pipeline.addLast("handler", clientHandler);

    return pipeline;
  }
}
