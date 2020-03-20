package ntalbs.velociraptor;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import ntalbs.velociraptor.handler.base.DefaultHandler;
import ntalbs.velociraptor.handler.echo.EchoHandler;
import ntalbs.velociraptor.handler.proxy.ProxyHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class VelociraptorVerticleTest {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
      new VelociraptorVerticle(new EchoHandler(), new ProxyHandler(), new DefaultHandler()),
      testContext.succeeding(id -> testContext.completeNow())
    );
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
