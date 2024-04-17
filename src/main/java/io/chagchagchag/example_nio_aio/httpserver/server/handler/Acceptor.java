package io.chagchagchag.example_nio_aio.httpserver.server.handler;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements EventHandler{
  private final Selector selector;
  private final ServerSocketChannel serverChannel;

  public Acceptor(Selector selector, ServerSocketChannel serverChannel) throws Exception {
    assert selector != null;
    assert serverChannel != null;

    this.selector = selector;
    this.serverChannel = serverChannel;
  }

  @Override
  public void handle() throws Exception {
    SocketChannel clientChannel = serverChannel.accept();
    new HttpEventHandler(selector, clientChannel);
  }
}
