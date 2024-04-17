package io.chagchagchag.example_nio_aio.httpserver.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JavaIOMultiClient {
  private static ExecutorService executorService = Executors.newFixedThreadPool(50);
  public static void main(String[] args) {
    System.out.println("start main");
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    long start = System.currentTimeMillis();

    for (int i = 0; i < 10; i++) {
      var future = CompletableFuture.runAsync(() -> {
        try (Socket socket = new Socket()) {
          socket.connect(new InetSocketAddress("localhost", 8080));

          OutputStream out = socket.getOutputStream();
          String requestBody = "This is client";
          out.write(requestBody.getBytes());
          out.flush();

          InputStream in = socket.getInputStream();
          byte[] responseBytes = new byte[1024];
          in.read(responseBytes);
          System.out.println("result: " + new String(responseBytes).trim());
        } catch (Exception e) {}
      }, executorService);

      futures.add(future);
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    executorService.shutdown();
    System.out.println("end main");
    long end = System.currentTimeMillis();
    System.out.println("duration: " + ((end - start) / 1000.0));
  }
}
