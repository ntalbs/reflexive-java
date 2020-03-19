package ntalbs.velociraptor.handler.proxy;

import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class ProxyHandler implements Handler<RoutingContext> {
  private static final String TARGET_ENDPOINT = "https://atv-ext.amazon.com";
  private static final Logger logger = LogManager.getLogger(ProxyHandler.class);

  private final WebClient webClient = WebClient.create(Vertx.vertx());

  @Override
  public void handle(RoutingContext routingContext) {
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
}
