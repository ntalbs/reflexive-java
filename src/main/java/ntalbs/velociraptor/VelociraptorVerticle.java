package ntalbs.velociraptor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import ntalbs.velociraptor.echo.EchoHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class VelociraptorVerticle extends AbstractVerticle {

  private static final int PORT = 8080;
  private static final String TARGET_ENDPOINT = "https://atv-ext.amazon.com";
  private static final Logger logger = LogManager.getLogger(VelociraptorVerticle.class);

  private final WebClient webClient = WebClient.create(Vertx.vertx());

  private final EchoHandler echoHandler;

  public VelociraptorVerticle(EchoHandler echoHandler) {
    this.echoHandler = echoHandler;
  }

  private void ping(RoutingContext rc) {
    rc.response().setStatusCode(200).end("Pong!");
    logger.info("HTTP 200: Pong");
  }

  private void forward(RoutingContext routingContext) {
    var req = routingContext.request();
    var inPath = req.path().replace("proxy", "cdp");
    var query = req.query();

    var targetUri = TARGET_ENDPOINT + inPath + "?" + query;
    webClient.getAbs(targetUri)
      .ssl(true)
      .timeout(1000)
      .send(ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          req.response()
            .setStatusCode(ar.result().statusCode())
            .end(response.body());
          logger.info("HTTP {}: {} {}?{}", ar.result().statusCode(), req.method(), req.path(), req.query());
        } else {
          req.response()
            .setStatusCode(502)
            .end("Delegate request failed");
          logger.info("Failed to forward request: {} {}?{}, Cause: {}", req.method(), req.path(), req.query(), ar.cause());
        }
      });
  }

  private void notFound(RoutingContext routingContext) {
    var req = routingContext.request();

    var response = new JsonObject()
      .put("method", req.method().name())
      .put("path", req.path())
      .put("status", 404)
      .put("message", "Not found")
      .encode();

    req.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(404)
      .end(response);
    logger.info("HTTP 404: {} {}?{}", req.method(), req.path(), req.query());
  }

  @Override
  public void start(Promise<Void> startPromise) {
    var router = Router.router(vertx);

    router.route("/ping").handler(this::ping);
    router.route("/echo*").handler(echoHandler);
    router.route("/proxy/*").handler(this::forward);
    router.route("/test*").handler(this::test);
    router.route("/*").handler(this::notFound);

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

  private void test(RoutingContext routingContext) {
    var req = routingContext.request();

    var m = Map.of(
      "header1", List.of(1,2,3),
      "header2", "hello"
    );

    req.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(200)
      .end(new JsonObject().put("headers", m).encode());
  }
}
