package deoplice.processor.types;

import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import lombok.Builder;
import lombok.Value;


@Value
@Builder
public class Registry {
    Map<String, Array<API>> apis;
    Map<String, Array<Lens_.Lens>> lenses;

    public static Registry empty() {
        return Registry.builder().lenses(HashMap.empty()).apis(HashMap.empty()).build();
    }

    public Registry merge(Registry registry) {
        return Registry.builder()
                .lenses(this.lenses.merge(registry.getLenses()))
                .apis(this.apis.merge(registry.getApis()))
                .build();
    }
}
