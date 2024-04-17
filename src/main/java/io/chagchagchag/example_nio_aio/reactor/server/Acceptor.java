package io.chagchagchag.example_nio_aio.reactor.server;

import io.chagchagchag.example_nio_aio.reactor.server.handler.EventHandler;
import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements EventHandler {
  private final Selector selector;
  private final ServerSocketChannel serverChannel;
  public Acceptor(Selector selector, ServerSocketChannel serverChannel) throws IOException {
    assert selector != null;
    assert serverChannel != null;

    this.selector = selector;
    this.serverChannel = serverChannel;
  }

  @Override
  public void handle() throws Exception{
    SocketChannel clientChannel = serverChannel.accept();
    new TcpEventHandler(selector, clientChannel);
  }
}
