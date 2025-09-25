package ntalbs.reflexive;

import io.vertx.core.Vertx;

class ReflexiveMain {

  static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    EchoHandler echoHandler = new EchoHandler();
    vertx.deployVerticle(new ReflexiveServer(echoHandler));
  }
}
