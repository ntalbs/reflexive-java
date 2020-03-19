package ntalbs.velociraptor.handler.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

@Value.Immutable
interface DefaultResponse {
  @JsonProperty String method();
  @JsonProperty String path();
  @JsonProperty int status();
  @JsonProperty String message();
}
