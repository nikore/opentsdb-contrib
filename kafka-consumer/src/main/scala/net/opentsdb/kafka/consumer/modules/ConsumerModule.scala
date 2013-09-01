package net.opentsdb.kafka.consumer.modules

import com.google.inject.{Singleton, Provides, AbstractModule}
import net.codingwell.scalaguice.ScalaModule
import java.util.Properties
import com.google.inject.name.Names

class ConsumerModule(props: Properties) extends AbstractModule with ScalaModule {
  def configure() {
    Names.bindProperties(binder(), props)
  }

  @Provides @Singleton def provideProperties: Properties = {
    props
  }
}
