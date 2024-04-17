package io.chagchagchag.example_nio_aio.reactor.server;

import io.chagchagchag.example_nio_aio.reactor.server.handler.Acceptor;
import io.chagchagchag.example_nio_aio.reactor.server.handler.EventHandler;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Reactor implements Runnable{
  // reactor 의 조건 : 별도의 스레드에서 동작할 수 있어야 한다. 따라서 Runnable 을 implements

  private int port;
  private ServerSocketChannel serverChannel;
  private Selector selector;
  private Acceptor acceptor;


  public Reactor(int port) throws Exception {
    this.port = port;
    this.serverChannel = ServerSocketChannel.open();

    serverChannel.bind(new InetSocketAddress("localhost", port));
    serverChannel.configureBlocking(false);

    this.selector = Selector.open();

    this.acceptor = new Acceptor(selector, serverChannel);
    serverChannel.register(selector, SelectionKey.OP_ACCEPT).attach(acceptor);
  }

  private static ExecutorService executorService = Executors.newSingleThreadExecutor();
  @Override
  public void run() {
    executorService.submit(() -> {
      while(true){
        selector.select();
        Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

        while(selectedKeys.hasNext()){
          SelectionKey key = selectedKeys.next();
          selectedKeys.remove();

          dispatch(key); // dispatcher 에 SelectionKey 를 넘겨줌
        }
      }
    });
  }

  public void dispatch(SelectionKey selectionKey) throws Exception{
    // register 에서 attach 했던 Handler 객체를 가져옵니다.
    EventHandler eventHandler = (EventHandler) selectionKey.attachment();

    // 그리고 이 handler 를 통해서 handle 을 실행
    if(selectionKey.isReadable() || selectionKey.isAcceptable()){
      eventHandler.handle();
    }
  }
}
