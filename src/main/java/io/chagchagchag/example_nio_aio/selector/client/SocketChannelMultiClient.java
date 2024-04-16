package io.chagchagchag.example_nio_aio.selector.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SocketChannelMultiClient {
  public static void main(String[] args) throws IOException {
    List<CompletableFuture> completableFutures = new ArrayList<>();
    System.out.println("start main");

    long start = System.currentTimeMillis();
    var executor = Executors.newFixedThreadPool(50);
    var counter = new AtomicInteger(0);

    for (var i = 0; i < 5000; i++) {
      var future = CompletableFuture.runAsync(() -> {
        try {
          try (var socketChannel = SocketChannel.open()) {
            var address = new InetSocketAddress("localhost", 8080);
            socketChannel.connect(address);

            String request = "This is client.";
            ByteBuffer requestBuffer = ByteBuffer.wrap(request.getBytes());
            socketChannel.write(requestBuffer);

            ByteBuffer res = ByteBuffer.allocateDirect(1024);
            while (socketChannel.read(res) > 0) {
              res.flip();
              res.clear();
            }
            counter.incrementAndGet();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }, executor);

      completableFutures.add(future);
    }

    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    executor.shutdown();
    System.out.println("end main");

    long end = System.currentTimeMillis();
    System.out.println("time: " + (end - start)/1000.0);
    System.out.println("count: " + counter.get());
  }
}
