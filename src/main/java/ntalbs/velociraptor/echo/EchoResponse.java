package ntalbs.velociraptor.echo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
interface EchoResponse {
  @JsonProperty String method();
  @JsonProperty String path();
  @JsonProperty Map<String, List<String>> params();
  @JsonProperty Map<String, List<String>> headers();
  @JsonProperty String body();
}
