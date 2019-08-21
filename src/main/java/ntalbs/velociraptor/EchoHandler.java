package ntalbs.velociraptor;

import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

@Singleton
public class EchoHandler implements Handler<HttpServerRequest> {

  @Override
  public void handle(HttpServerRequest req) {
    req.response()
      .putHeader("content-type", "text/plain")
      .end("Hello from Vert.x!\nXXX");
  }
}
