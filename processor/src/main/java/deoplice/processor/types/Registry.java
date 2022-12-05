package deoplice.processor.types;

import io.vavr.collection.Array;
import io.vavr.collection.Map;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class Registry {
    Map<String, Array<API>> apis;
    Map<String, Array<Lens_.Lens>> lenses;
}
