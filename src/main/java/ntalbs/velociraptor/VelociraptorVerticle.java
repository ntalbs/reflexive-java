package ntalbs.velociraptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

public class VelociraptorVerticle extends AbstractVerticle {

  private static final int PORT = 8080;
  private static final String TARGET_ENDPOINT = "https://atv-ext.amazon.com";
  private static final Logger logger = LogManager.getLogger(VelociraptorVerticle.class);

  private static final XmlMapper xmlMapper = new XmlMapper();

  private final WebClient webClient = WebClient.create(Vertx.vertx());

  private Map<String, List<String>> convert(MultiMap src) {
    return src.entries().stream()
      .collect(groupingBy(
        Map.Entry::getKey,
        LinkedHashMap::new,
        mapping(Map.Entry::getValue, toList())
      ));
  }

  private static String contentType(String format) {
    switch (format) {
      case "xml": return "application/xml";
      default: return "application/json";
    }
  }

  private static Buffer toJson(Object response) {
    return JsonObject.mapFrom(response).toBuffer();
  }

  private static Buffer toXml(Object response) {
    try {
      return Buffer.buffer(
        xmlMapper.writer()
          .withRootName("Response")
          .writeValueAsBytes(response)
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private static Buffer render(String format, Object response) {
    switch (format) {
      case "xml": return toXml(response);
      default: return toJson(response);
    }
  }

  private static String renderError(String format) {
    switch (format) {
      case "xml": return "<Response>Error</Response>";
      default: return "{\"Response\": \"Error\"}";
    }
  }

  private void ping(RoutingContext rc) {
    rc.response().setStatusCode(200).end("Pong!");
    logger.info("HTTP 200: Pong");
  }

  private void echo(RoutingContext routingContext) {
    var req = routingContext.request();
    req.bodyHandler(buf -> {
      EchoResponse response = ImmutableEchoResponse.builder()
        .method(req.method().name())
        .path(req.path())
        .headers(convert(req.headers()))
        .params(convert(req.params()))
        .body(buf.toString())
        .build();

      var format = req.getParam("format");
      try {
        var render = render(format, response);
        req.response()
          .putHeader("content-type", contentType(format))
          .end(render);
        logger.info("HTTP 200: {} {}?{}", req.method(), req.path(), req.query());
      } catch (Exception e) {
        req.response()
          .setStatusCode(500)
          .putHeader("content-type", contentType(format))
          .end(renderError(format));
        logger.error("HTTP 500: {} {}?{}", req.method(), req.path(), req.query());
      }
    });
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

    var response = ImmutableErrorResponse.builder()
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
    var router = Router.router(vertx);

    router.route("/ping").handler(this::ping);
    router.route("/echo*").handler(this::echo);
    router.route("/proxy/*").handler(this::forward);
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
}
