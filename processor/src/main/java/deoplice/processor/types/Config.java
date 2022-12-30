package deoplice.processor.types;

import deoplice.annotation.Updatable;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Delegate;


@Value
@Builder
public class Config {
    @Delegate Updatable annotation;
}
