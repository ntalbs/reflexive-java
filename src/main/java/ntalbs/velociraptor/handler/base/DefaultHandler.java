package ntalbs.velociraptor.handler.base;

import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class DefaultHandler implements Handler<RoutingContext> {
  private static final Logger logger = LogManager.getLogger(DefaultHandler.class);

  @Override
  public void handle(RoutingContext routingContext) {
    HttpServerRequest req = routingContext.request();

    DefaultResponse response = ImmutableDefaultResponse.builder()
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
}
