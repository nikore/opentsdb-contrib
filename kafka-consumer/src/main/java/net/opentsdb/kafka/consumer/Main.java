package net.opentsdb.kafka.consumer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.opentsdb.client.netty.TsdbClient;
import net.opentsdb.client.netty.modules.TsdbClientModule;
import net.opentsdb.kafka.consumer.modules.ConsumerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Main method controls the life cycle of the app
 */
public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) throws Exception {

    File config = new File(args[0]);
    if (!config.canRead()) {
      System.err.println("Cannot open config file: " + config);
      System.exit(1);
    }

    Properties props = new Properties();
    props.load(new BufferedInputStream(new FileInputStream(config)));

    logger.info("Loaded the file properties: {}", props.toString());

    Injector injector = Guice.createInjector(new ConsumerModule(props), new TsdbClientModule(props));

    logger.info("Starting TSDB Client...");
    final TsdbClient server = injector.getInstance(TsdbClient.class);
    server.startAndWait();

    logger.info("Starting TSDB Consumer...");
    final TsdbConsumer consumer = injector.getInstance(TsdbConsumer.class);
    consumer.startAndWait();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        logger.info("Shutting down server");
        consumer.stopAndWait();
        server.stopAndWait();
      }
    });
  }
}
