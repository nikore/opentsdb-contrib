package net.opentsdb.proxy.clients


trait Client {
  def sendMessage(message: String)
}
