package ntalbs.velociraptor;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Singleton
public class VelociraptorVerticle extends AbstractVerticle {

  private static final int PORT = 8080;
  private static final String TARGET_ENDPOINT = "https://atv-ext.amazon.com";
  private static final Logger logger = LogManager.getLogger(VelociraptorVerticle.class);

  private final WebClient webClient = WebClient.create(Vertx.vertx());

  private Map<String, List<String>> convert(MultiMap src) {
    return src.entries().stream()
      .collect(groupingBy(
        Map.Entry::getKey,
        LinkedHashMap::new,
        mapping(Map.Entry::getValue, toList())
      ));
  }

  private void handlePing(RoutingContext rc) {
    rc.response().setStatusCode(200).end("Succeed");
  }

  private void handleEcho(RoutingContext routingContext) {
    HttpServerRequest req = routingContext.request();
    req.bodyHandler(buf -> {
      EchoResponse response = ImmutableEchoResponse.builder()
        .method(req.method().name())
        .path(req.path())
        .headers(convert(req.headers()))
        .params(convert(req.params()))
        .body(buf.toString())
        .build();

      req.response()
        .putHeader("content-type", "application/json")
        .end(JsonObject.mapFrom(response).toBuffer());
      logger.info("HTTP 200: {} {}?{}", req.method(), req.path(), req.query());
    });
  }

  private void handleProxy(RoutingContext routingContext) {
    HttpServerRequest req = routingContext.request();
    String inPath = req.path().replace("proxy", "cdp");
    String query = req.query();

    String targetUri = TARGET_ENDPOINT + inPath + "?" + query;
    logger.info("Delegate request -> target URI: " + targetUri);
    webClient.getAbs(targetUri)
      .ssl(true)
      .timeout(1000)
      .send(ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          req.response()
            .end(response.body());
          logger.info("Delegate request succeeded.");
        } else {
          logger.info("Delegate request failed.");
          req.response()
            .setStatusCode(ar.result().statusCode())
            .end("Delegate request failed");
        }
      });
  }

  private void handleNotFound(RoutingContext routingContext) {
    HttpServerRequest req = routingContext.request();

    ErrorResponse response = ImmutableErrorResponse.builder()
      .method(req.method().name())
      .path(req.path())
      .status(404)
      .message("Not found")
      .build();

    req.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(404)
      .end(JsonObject.mapFrom(response).toBuffer());
    logger.info("HTTP 404: {} {}?{}", req.method(), req.path(), req.query());
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    router.route("/ping").handler(this::handlePing);
    router.route("/echo*").handler(this::handleEcho);
    router.route("/proxy/*").handler(this::handleProxy);
    router.route("/*").handler(this::handleNotFound);

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
