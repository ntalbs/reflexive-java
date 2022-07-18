package ntalbs.reflexive;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class EchoHandler implements Handler<RoutingContext> {
  private static final Logger logger = LogManager.getLogger(EchoHandler.class);

  @Override
  public void handle(RoutingContext routingContext) {
    var req = routingContext.request();
    req.bodyHandler(buf -> {
      var response = new JsonObject()
        .put("method", req.method().name())
        .put("path", req.path())
        .put("headers", convert(req.headers()))
        .put("params", convert(req.params()))
        .put("body", body(buf))
        .encode();

      req.response()
        .putHeader("content-type", "application/json")
        .end(response);
      logger.info("HTTP 200: {} {}?{}", req.method(), req.path(), req.query());
    });
  }

  private Object body(Buffer buf) {
    try {
      return new JsonObject(buf);
    } catch (Exception x) {
      return buf.toString();
    }
  }

  private Map<String, ?> convert(MultiMap src) {
    return src.entries().stream()
      .collect(groupingBy(
        Map.Entry::getKey,
        LinkedHashMap::new,
        mapping(Map.Entry::getValue, toList())
      )).entrySet().stream()
      .map(e -> Map.entry(e.getKey(), toListOrVal(e.getValue())))
      .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Object toListOrVal(List<String> list) {
    if (list.size() == 1) {
      return list.get(0);
    } else {
      return list;
    }
  }
}
