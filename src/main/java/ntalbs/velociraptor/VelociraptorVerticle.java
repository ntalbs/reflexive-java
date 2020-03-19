package ntalbs.velociraptor;

import com.google.inject.Inject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import javax.inject.Singleton;

@Singleton
public class VelociraptorVerticle extends AbstractVerticle {

  private static final int PORT = 8080;
  private final Handler<RoutingContext> echoHandler;

  @Inject
  public VelociraptorVerticle(Handler<RoutingContext> echoHandler) {
    this.echoHandler = echoHandler;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    router.route("/*").handler(rc -> {
      rc.response()
        .putHeader("content-type", "application/json");
      rc.next();
    });
    router.route("/echo").handler(echoHandler);
    router.route("/*").handler(rc -> rc.response()
        .setStatusCode(404)
        .end("{ status: 404, path: \"" + rc.request().path()+ "\" }")
    );

    vertx.createHttpServer()
      .requestHandler(router)
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
