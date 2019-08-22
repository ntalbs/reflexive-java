package ntalbs.velociraptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import ntalbs.velociraptor.echo.EchoHandler;
import ntalbs.velociraptor.echo.EchoVerticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class EchoVerticleTest {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
      new EchoVerticle(new EchoHandler(new ObjectMapper())),
      testContext.succeeding(id -> testContext.completeNow())
    );
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
