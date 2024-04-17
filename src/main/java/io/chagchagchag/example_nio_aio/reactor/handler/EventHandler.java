package io.chagchagchag.example_nio_aio.reactor.handler;

// Acceptor, Handler 에 대한 추상 기능을 제공하는 타입
// Acceptor : Accept 하는 역할을 전담
// Handler : READ 이벤트에 집중해서 처리를 전담
public interface EventHandler {
  void handle() throws Exception;
}
