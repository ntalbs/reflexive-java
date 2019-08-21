package ntalbs.velociraptor;

import com.google.inject.Inject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import javax.inject.Singleton;

@Singleton
public class EchoVerticle extends AbstractVerticle {

  private static final int PORT = 8080;
  private Handler<HttpServerRequest> echoHandler;

  @Inject
  public EchoVerticle(Handler<HttpServerRequest> echoHandler) {
    this.echoHandler = echoHandler;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    vertx.createHttpServer()
      .requestHandler(echoHandler)
      .listen(PORT, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.printf("HTTP server started on port %d\n", PORT);
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
