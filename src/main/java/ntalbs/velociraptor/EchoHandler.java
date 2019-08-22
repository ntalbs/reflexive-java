package ntalbs.velociraptor;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.vertx.core.Handler;
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

  @Override
  public void handle(HttpServerRequest req) {
    Map<String, List<String>> headers = req.headers().entries().stream()
      .collect(groupingBy(
        Map.Entry::getKey,
        LinkedHashMap::new,
        mapping(Map.Entry::getValue, toList())
      ));
    Map<String, List<String>> params = req.params().entries().stream()
      .collect(groupingBy(
        Map.Entry::getKey,
        LinkedHashMap::new,
        mapping(Map.Entry::getValue, toList())
      ));

    EchoResponse response = ImmutableEchoResponse.builder()
      .method(req.method().name())
      .path(req.path())
      .headers(headers)
      .params(params)
      .body("Need to be set...")
      .build();

    try {
      req.response()
        .putHeader("content-type", "application/json")
        .end(objectMapper.writeValueAsString(response));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
