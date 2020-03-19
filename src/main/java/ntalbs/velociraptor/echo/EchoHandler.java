package ntalbs.velociraptor.echo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
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
  private final ObjectMapper mapper;

  @Inject
  public EchoHandler(ObjectMapper mapper) {
    this.mapper = mapper;
  }

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

      try {
        req.response()
          .putHeader("content-type", "application/json")
          .end(mapper.writeValueAsString(response));
        logger.info("HTTP 200: {} {}?{}", req.method(), req.path(), req.query());
      } catch (JsonProcessingException e) {
        req.response().reset();
        req.response().setStatusCode(500).end();
        logger.info("HTTP 500: {} {}", req.method(), req.path());
        logger.info("Exception thrown while handling request.", e);
      }
    });
  }
}
