package ntalbs.reflexive;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReflexiveServer extends VerticleBase {

  private static final int PORT = 3000;
  private static final Logger logger = LogManager.getLogger(ReflexiveServer.class);

  private final EchoHandler echoHandler;

  public ReflexiveServer(EchoHandler echoHandler) {
    this.echoHandler = echoHandler;
  }

  public Future<?> start() {
    var router = Router.router(vertx);
    router.route("/*").handler(echoHandler);

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(PORT)
      .onSuccess(_ -> logger.info("HTTP server started on port {}", PORT))
      .onFailure(x -> logger.error("Failed to start HTTP server: {0}", x))
    ;
  }
}
