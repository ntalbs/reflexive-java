package ntalbs.velociraptor;

import com.google.inject.Inject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import javax.inject.Singleton;
import ntalbs.velociraptor.handler.base.DefaultHandler;
import ntalbs.velociraptor.handler.echo.EchoHandler;
import ntalbs.velociraptor.handler.proxy.ProxyHandler;

@Singleton
public class VelociraptorVerticle extends AbstractVerticle {

  private static final int PORT = 8080;
  private final EchoHandler echoHandler;
  private final ProxyHandler proxyHandler;
  private final DefaultHandler defaultHandler;

  @Inject
  public VelociraptorVerticle(EchoHandler echoHandler, ProxyHandler proxyHandler, DefaultHandler defaultHandler) {
    this.echoHandler = echoHandler;
    this.defaultHandler = defaultHandler;
    this.proxyHandler = proxyHandler;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    router.route("/echo").handler(echoHandler);
    router.route("/proxy/*").handler(proxyHandler);
    router.route("/*").handler(defaultHandler);

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
