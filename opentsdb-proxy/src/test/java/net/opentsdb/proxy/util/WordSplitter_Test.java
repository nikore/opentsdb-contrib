package net.opentsdb.proxy.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordSplitter_Test {

  @Test
  public void testSplit() throws Exception {
    WordSplitter splitter = new WordSplitter();


    String[] test = (String[]) splitter.decode(null, null, "Hello Matt");

    assertTrue(test != null);
    assertEquals(test.length, 2);
  }
}
