package net.opentsdb.proxy.clients;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import net.opentsdb.client.netty.TsdbClient;
import net.opentsdb.client.netty.modules.TsdbClientModule;

import java.util.Properties;

public class ProxyClient implements Client {
  private final TsdbClient tsdbClient;

  @Inject
  public ProxyClient(Properties properties) {
    Injector injector = Guice.createInjector(new TsdbClientModule(properties));

    this.tsdbClient = injector.getInstance(TsdbClient.class);
    this.tsdbClient.startAndWait();
  }

  @Override
  public void sendMessage(String message) {
    tsdbClient.send(message);
  }
}
