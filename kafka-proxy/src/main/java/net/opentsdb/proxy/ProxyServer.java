package net.opentsdb.proxy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.opentsdb.proxy.modules.ProxyModule;
import net.opentsdb.proxy.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ProxyServer {
  private static final Logger logger = LoggerFactory.getLogger(ProxyServer.class);

  public static void main(String... args) throws Exception {

    File config = new File(args[0]);
    if (!config.canRead()) {
      System.err.println("Cannot open config file: " + config);
      System.exit(1);
    }

    Properties props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(config)));

    logger.info("Loaded the file properties: {}", props.toString());

    Injector injector = Guice.createInjector(new ProxyModule(props));

    final NettyServer server = injector.getInstance(NettyServer.class);

    logger.info("Starting Server on port 8080 ....");
    server.startAndWait();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        logger.info("Shutting down server");
        server.stopAndWait();
      }
    });
  }
}
