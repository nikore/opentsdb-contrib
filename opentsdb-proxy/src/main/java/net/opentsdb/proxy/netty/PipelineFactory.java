package net.opentsdb.proxy.netty;

import com.google.inject.Inject;
import net.opentsdb.proxy.clients.Client;
import net.opentsdb.proxy.util.WordSplitter;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class PipelineFactory implements ChannelPipelineFactory {

  // Those are entirely stateless and thus a single instance is needed.
  private static final StringEncoder ENCODER = new StringEncoder();
  private static final WordSplitter DECODER = new WordSplitter();

  // Those are sharable but maintain some state, so a single instance per
  // PipelineFactory is needed.
  private final ConnectionManager connmgr = new ConnectionManager();

  /**
   * Stateless handler for RPCs.
   */
  private final RpcHandler rpchandler;

  /**
   * Constructor.
   */
  @Inject
  public PipelineFactory(Client client) {
    this.rpchandler = new RpcHandler(client);
  }

  @Override
  public ChannelPipeline getPipeline() throws Exception {
    final ChannelPipeline pipeline = Channels.pipeline();

    pipeline.addLast("connmgr", connmgr);
    pipeline.addLast("framer", new LineBasedFrameDecoder(1024));
    pipeline.addLast("encoder", ENCODER);
    pipeline.addLast("decoder", DECODER);
    pipeline.addLast("handler", rpchandler);
    return pipeline;
  }
}



