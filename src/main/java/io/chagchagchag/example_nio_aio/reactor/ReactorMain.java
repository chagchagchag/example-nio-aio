package io.chagchagchag.example_nio_aio.reactor;

import io.chagchagchag.example_nio_aio.reactor.server.Reactor;
import lombok.SneakyThrows;

public class ReactorMain {
  @SneakyThrows
  public static void main(String[] args) {
    System.out.println(" >>> started ");
    Reactor reactor = new Reactor(8080);
    reactor.run(); //
    System.out.println(" >>> finish ");
  }
}
