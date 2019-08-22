package ntalbs.velociraptor;

import static com.google.inject.Guice.createInjector;

import com.google.inject.Injector;
import io.vertx.core.Vertx;
import ntalbs.velociraptor.echo.EchoVerticle;

public class VelociraptorMain {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    Injector injector = createInjector(new VelociraptorModule());
    vertx.deployVerticle(injector.getInstance(EchoVerticle.class));
  }
}
