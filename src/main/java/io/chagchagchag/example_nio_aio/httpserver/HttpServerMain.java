package io.chagchagchag.example_nio_aio.httpserver;

import io.chagchagchag.example_nio_aio.httpserver.server.EventLoop;
import java.util.List;

public class HttpServerMain {
  public static void main(String[] args) throws Exception{
    System.out.println(">>> started ");

    List<EventLoop> eventLoopList = List.of(
        new EventLoop(8080)
    );

    eventLoopList.forEach(eventLoop -> eventLoop.run());

    System.out.println(">>> finished");
  }
}
