package ntalbs.velociraptor;

import io.vertx.core.Vertx;

class VelociraptorMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new VelociraptorVerticle());
  }
}
