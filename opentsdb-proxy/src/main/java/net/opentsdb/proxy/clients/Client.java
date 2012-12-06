package net.opentsdb.proxy.clients;

public interface Client {

  void sendMessage(String message);
}
