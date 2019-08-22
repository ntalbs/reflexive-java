package ntalbs.velociraptor;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class EchoHandler implements Handler<HttpServerRequest> {

  private final ObjectMapper objectMapper;

  @Inject
  public EchoHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
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
  public void handle(HttpServerRequest req) {
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
          .end(objectMapper.writeValueAsString(response));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    });
  }
}
