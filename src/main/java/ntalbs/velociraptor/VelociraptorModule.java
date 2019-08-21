package ntalbs.velociraptor;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class VelociraptorModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  @Singleton
  @Provides
  public Handler<HttpServerRequest> echoHandler() {
    return new EchoHandler();
  }

}
