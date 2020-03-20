package ntalbs.velociraptor.handler.echo;

import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Singleton
public class EchoHandler implements Handler<RoutingContext> {

  private static final Logger logger = LogManager.getLogger(EchoHandler.class);

  private Map<String, List<String>> convert(MultiMap src) {
    return src.entries().stream()
      .collect(groupingBy(
        Map.Entry::getKey,
        LinkedHashMap::new,
        mapping(Map.Entry::getValue, toList())
      ));
  }

  @Override
  public void handle(RoutingContext routingContext) {
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
}
