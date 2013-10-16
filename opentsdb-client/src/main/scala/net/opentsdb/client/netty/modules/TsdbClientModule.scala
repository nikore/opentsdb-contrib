package net.opentsdb.client.netty.modules

import com.google.inject.{Singleton, Provides, AbstractModule}
import java.net.InetSocketAddress
import org.jboss.netty.channel.ChannelFactory
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import java.util.concurrent.Executors
import net.opentsdb.client.netty.TsdbClientPipelineFactory
import net.codingwell.scalaguice.ScalaModule
import java.util.Properties
import com.google.inject.name.Named

class TsdbClientModule(props: Properties) extends AbstractModule with ScalaModule {
  def configure() {
    bind[TsdbClientPipelineFactory].asEagerSingleton()
  }

  @Provides @Singleton def provideSocketAddress(@Named("tsdb.port") port: Int, @Named("tsdb.host") host: String) : InetSocketAddress = new InetSocketAddress(host, port)

  @Provides @Singleton def provideChannelFactory : ChannelFactory = new NioClientSocketChannelFactory()
}
