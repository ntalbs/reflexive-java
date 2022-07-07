package ntalbs.reflexive;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReflexiveVerticle extends AbstractVerticle {

  private static final int PORT = 3000;
  private static final Logger logger = LogManager.getLogger(ReflexiveVerticle.class);

  private final EchoHandler echoHandler;

  public ReflexiveVerticle(EchoHandler echoHandler) {
    this.echoHandler = echoHandler;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    var router = Router.router(vertx);
    router.route("/*").handler(echoHandler);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(PORT, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          logger.info("HTTP server started on port {}\n", PORT);
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
