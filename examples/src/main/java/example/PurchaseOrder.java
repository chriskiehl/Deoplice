package example;

import deoplice.annotation.Updatable;
import io.vavr.collection.Array;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@Value
@With
@Builder
@Updatable
public class PurchaseOrder {
    String number;
    Approval approval;

    @With
    @Value
    @Builder
    public static class Approval {
        ApprovalStatus status;
        Array<String> comments;
        Confirmation confirmation;
    }
    @With
    @Value
    @Builder
    public static class Confirmation {
        UserAlias alias;
        LocalDateTime updatedOn;
    }

    @With
    @Value(staticConstructor = "of")
    public static class UserAlias {
        String value;
    }

    public static enum ApprovalStatus {
        PENDING,
        COMPLETED;
    }
}
