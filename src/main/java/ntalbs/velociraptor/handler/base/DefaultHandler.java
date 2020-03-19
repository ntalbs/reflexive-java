package ntalbs.velociraptor.handler.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class DefaultHandler implements Handler<RoutingContext> {
  private static final Logger logger = LogManager.getLogger(DefaultHandler.class);
  private final ObjectMapper mapper;

  @Inject
  public DefaultHandler(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    HttpServerRequest req = routingContext.request();

    DefaultResponse response = ImmutableDefaultResponse.builder()
      .method(req.method().name())
      .path(req.path())
      .status(404)
      .message("Not found")
      .build();

    try {
      req.response()
        .putHeader("content-type", "application/json")
        .end(mapper.writeValueAsString(response));
      logger.info("HTTP 404: {} {}?{}", req.method(), req.path(), req.query());
    } catch (JsonProcessingException e) {
      req.response().reset();
      req.response().setStatusCode(500).end();
      logger.info("HTTP 500: {} {}", req.method(), req.path());
      logger.info("Exception thrown while handling request.", e);
    }
  }
}
