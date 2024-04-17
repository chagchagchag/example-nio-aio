package io.chagchagchag.example_nio_aio.reactor.server.handler;

import io.chagchagchag.example_nio_aio.reactor.server.handler.EventHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpEventHandler implements EventHandler {
  private final Selector selector;
  private final SocketChannel clientChannel;

  public TcpEventHandler(Selector selector, SocketChannel clientChannel)
  throws Exception {
    this.selector = selector;
    this.clientChannel = clientChannel;

    this.clientChannel.configureBlocking(false);
    this.clientChannel.register(selector, SelectionKey.OP_READ).attach(this);
  }

  @Override
  public void handle() throws Exception{
    String requestBody = handleRequest(this.clientChannel);
    sendResponse(clientChannel, requestBody);
  }

  public static String handleRequest(SocketChannel clientSocket) throws IOException {
    ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
    clientSocket.read(requestByteBuffer);

    requestByteBuffer.flip();
    String requestBody = StandardCharsets.UTF_8.decode(requestByteBuffer).toString();

    return requestBody;
  }

  private static ExecutorService executorService = Executors.newFixedThreadPool(50);

  public static void sendResponse(SocketChannel clientSocket, String requestBody) throws IOException {
    CompletableFuture.runAsync(() -> {
      try{
        Thread.sleep(10);

        String content = "received : " + requestBody;
        ByteBuffer responseByteBuffer = ByteBuffer.wrap(content.getBytes());
        clientSocket.write(responseByteBuffer);
        clientSocket.close();

      } catch (Exception e){}
    }, executorService);
  }
}
