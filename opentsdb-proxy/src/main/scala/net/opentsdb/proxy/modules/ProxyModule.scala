package net.opentsdb.proxy.modules

import java.util.Properties
import com.google.inject.{Injector, Singleton, Provides, AbstractModule}
import net.codingwell.scalaguice.ScalaModule
import net.opentsdb.proxy.clients.Client
import java.net.{InetSocketAddress, SocketAddress}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroup}
import org.jboss.netty.channel.ChannelFactory
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.util.concurrent.Executors
import net.opentsdb.proxy.netty.PipelineFactory
import com.google.inject.name.{Names, Named}

class ProxyModule(props: Properties) extends AbstractModule with ScalaModule {
  def configure() {
    Names.bindProperties(binder(), props)
    bind(classOf[PipelineFactory]).asEagerSingleton()
  }

  @Provides @Singleton def provideClient(injector: Injector, @Named("tsdb.proxy.client.class") clientClass: Class[Client]): Client = injector.getInstance(clientClass)

  @Provides @Singleton def provideProperties: Properties = props

  @Provides @Singleton def provideSocketAddress(@Named("tsdb.proxy.port") port: Int): SocketAddress = new InetSocketAddress(port)

  @Provides @Singleton def provideChannelGroup: ChannelGroup = new DefaultChannelGroup("opentsdb-proxy")

  @Provides @Singleton def provideChannelFactory: ChannelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool())
}
