package net.opentsdb.proxy.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.opentsdb.proxy.clients.Client;
import net.opentsdb.proxy.netty.PipelineFactory;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * Guice module for binding instances together
 */
public class ProxyModule extends AbstractModule {
  private static final String PROXY_PORT = "tsdb.proxy.port";
  private static final String CLIENT_CLASS = "tsdb.proxy.client.class";

  private final Properties properties;

  public ProxyModule(Properties properties) {
    this.properties = properties;
  }

  @Override
  protected void configure() {
    //Magic! loads the properties as something that can be accessed via @Named
    Names.bindProperties(binder(), properties);
    bind(PipelineFactory.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  Client provideClient(Injector injector, @Named(CLIENT_CLASS) Class<Client> clientClass) {
    return injector.getInstance(clientClass);
  }

  @Provides
  @Singleton
  Properties provideProperties() {
    return properties;
  }

  @Provides
  @Singleton
  SocketAddress provideSocketAddress(@Named(PROXY_PORT) Integer port) {
    return new InetSocketAddress(port);
  }

  @Provides
  @Singleton
  ChannelGroup provideChannelGroup() {
    return new DefaultChannelGroup("opentsdb-proxy");
  }

  @Provides
  @Singleton
  ChannelFactory provideChannelFactory() {
    return new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
  }
}
