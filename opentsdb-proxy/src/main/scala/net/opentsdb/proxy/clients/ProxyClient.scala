package net.opentsdb.proxy.clients

import com.google.inject.{Guice, Inject}
import java.util.Properties
import net.opentsdb.client.netty.modules.TsdbClientModule
import net.opentsdb.client.netty.TsdbClient

class ProxyClient @Inject() (props: Properties) extends Client {
  val injector = Guice.createInjector(new TsdbClientModule(props))

  val tsdbclient = injector.getInstance(classOf[TsdbClient])
  tsdbclient.startAndWait()

  def sendMessage(message: String) {
    tsdbclient.send(message)
  }
}
