package io.chagchagchag.example_nio_aio.httpserver.server.handler;

import io.chagchagchag.example_nio_aio.httpserver.codec.MsgCodec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpEventHandler implements EventHandler {
  private final Selector selector;
  private final SocketChannel clientChannel;
  private final MsgCodec msgCodec;

  public HttpEventHandler(Selector selector, SocketChannel clientChannel)
  throws Exception {
    this.selector = selector;
    this.clientChannel = clientChannel;

    this.clientChannel.configureBlocking(false);
    this.clientChannel.register(selector, SelectionKey.OP_READ).attach(this);
    this.msgCodec = new MsgCodec();
  }

  @Override
  public void handle() throws Exception {
    try{
      String requestBody = handleRequest(this.clientChannel);
      System.out.println("requestBody :: " + requestBody);
      sendResponse(clientChannel, requestBody);
    }
    catch (Exception e){
      e.printStackTrace();
      System.out.println("message = " + e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  public String handleRequest(SocketChannel clientSocket) throws IOException {
    ByteBuffer requestByteBuffer = ByteBuffer.allocateDirect(1024);
    clientSocket.read(requestByteBuffer);
    return msgCodec.decode(requestByteBuffer);
  }

  private static ExecutorService executorService = Executors.newFixedThreadPool(50);

  public void sendResponse(SocketChannel clientSocket, String requestBody) throws IOException {
    CompletableFuture.runAsync(() -> {
      try{
        Thread.sleep(10);

        ByteBuffer responseByteBuffer = msgCodec.encode(requestBody);
        clientSocket.write(responseByteBuffer);
        clientSocket.close();

      } catch (Exception e){}
    }, executorService);
  }
}
