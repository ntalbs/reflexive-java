package ntalbs.velociraptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class VelociraptorModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(new TypeLiteral<Handler<HttpServerRequest>>(){}).to(EchoHandler.class);
  }

  @Provides
  @Singleton
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
