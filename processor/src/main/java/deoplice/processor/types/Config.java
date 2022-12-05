package deoplice.processor.types;

import lombok.Value;

@Value
public class Config {
    String lensPrefix = "$";
    String apiPrefix = "set";
    String groupPostfix = "Lens";
}
