package ntalbs.velociraptor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {
  private static final int PORT = 8080;

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(PORT, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.printf("HTTP server started on port %d\n", PORT);
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
