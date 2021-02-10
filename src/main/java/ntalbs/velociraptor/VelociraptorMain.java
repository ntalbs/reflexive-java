package ntalbs.velociraptor;

import io.vertx.core.Vertx;
import ntalbs.velociraptor.echo.EchoHandler;

class VelociraptorMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    EchoHandler echoHandler = new EchoHandler();
    vertx.deployVerticle(new VelociraptorVerticle(echoHandler));
  }
}
