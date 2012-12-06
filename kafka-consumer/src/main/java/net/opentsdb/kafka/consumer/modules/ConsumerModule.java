package net.opentsdb.kafka.consumer.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import java.util.Properties;

/**
 * Guice Module used to bind instances
 */
public class ConsumerModule extends AbstractModule {
  private final Properties properties;

  public ConsumerModule(Properties properties) {
    this.properties = properties;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);
  }

  @Provides
  @Singleton
  Properties provideProperties() {
    return properties;
  }
}
