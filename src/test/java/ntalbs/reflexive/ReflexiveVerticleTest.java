package ntalbs.reflexive;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class ReflexiveVerticleTest {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
      new ReflexiveVerticle(new EchoHandler()),
      testContext.succeeding(id -> testContext.completeNow())
    );
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
    testContext.completeNow();
  }
}
