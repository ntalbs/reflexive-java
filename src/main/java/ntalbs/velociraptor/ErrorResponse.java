package ntalbs.velociraptor;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

@Value.Immutable
interface ErrorResponse {
  @JsonProperty String method();
  @JsonProperty String path();
  @JsonProperty int status();
  @JsonProperty String message();
}
