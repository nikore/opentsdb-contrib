package net.opentsdb.client.netty.modules

import com.google.inject.{Singleton, Provides, AbstractModule}
import java.net.InetSocketAddress
import org.jboss.netty.channel.ChannelFactory
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import java.util.concurrent.Executors
import com.typesafe.config.{ConfigFactory, Config}
import net.opentsdb.client.netty.TsdbClientPipelineFactory
import net.codingwell.scalaguice.ScalaModule

class TsdbClientModule extends AbstractModule with ScalaModule {
  private final val TSDB_PORT : String = "tsdb.port"
  private final val TSDB_HOST : String = "tsdb.host"
  private final val conf: Config = ConfigFactory.load

  def configure() {
    bind[TsdbClientPipelineFactory].asEagerSingleton()
  }

  @Provides @Singleton def provideSocketAddress : InetSocketAddress = new InetSocketAddress(conf.getString(TSDB_HOST), conf.getInt(TSDB_PORT))

  @Provides @Singleton def provideChannelFactory : ChannelFactory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool())
}
