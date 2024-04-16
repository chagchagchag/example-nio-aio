package io.chagchagchag.example_nio_aio.selector.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectorMultiServerMain {
  public static void main(String[] args) {
    try{
      try(
          ServerSocketChannel serverChannel = ServerSocketChannel.open();
          Selector selector = Selector.open();
      ){
        serverChannel.bind(new InetSocketAddress("localhost", 8080));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
          selector.select();
          Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

          while(selectedKeys.hasNext()){
            SelectionKey key = selectedKeys.next();
            selectedKeys.remove();

            if(key.isAcceptable()){
              SocketChannel clientSocket = ((ServerSocketChannel)key.channel()).accept();
              clientSocket.configureBlocking(false);
              clientSocket.register(selector, SelectionKey.OP_READ);
            }
            else if(key.isReadable()){
              SocketChannel clientSocket = (SocketChannel) key.channel();
              String requestBody = handleRequest(clientSocket);
              sendResponse(clientSocket, requestBody);
            }
          }
        }
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }
    finally {
      executorService.shutdown();
    }

  }

  public static String handleRequest(SocketChannel clientSocket) throws IOException{
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
