package example;

import example.PurchaseOrder.Approval;
import example.PurchaseOrder.ApprovalStatus;
import example.PurchaseOrder.UserAlias;
import io.vavr.collection.Array;

import java.time.LocalDateTime;

import static example.PurchaseOrder.ApprovalStatus.PENDING;
import static example.PurchaseOrderAPI.*;

/**
 * Basic demo of using the generated API to perform complex updates a POJO.
 */
public class PurchaseOrderExample {
    public static void main(String... args) {
        PurchaseOrder order = buildPurchaseOrder();

        // Let's modify the comments on our Order's approval
        // We'll first do it with vanilla With'ers.
        PurchaseOrder updatedByWithers = order.withApproval(
                order.getApproval().withComments
                        (order.getApproval().getComments().append("New Comment!")
        ));
        // Bleh! Gross!
        // Now let's do the exact same thing with Deoplice's generated API
        PurchaseOrder updatedByDeoplice = appendApprovalComments("New Comment!").apply(order);
        // Whoa! Sick!
        // All of it in one line!

        // Now let's do something super-duper complicated.
        //   1. add a new comment
        //   2. Mark the approval as complete.
        //   3. Set the Confirmation date to today
        PurchaseOrder updatedAgain = appendApprovalComments("New Comment!")
                .andThen(setApprovalStatus(ApprovalStatus.COMPLETED))
                .andThen(setApprovalConfirmationUpdatedOn(LocalDateTime.now()))
                .apply(order);

        // Neato!
    }


    /**
     * An arbitrary instantiation of a PurchaseOrder type.
     */
    public static PurchaseOrder buildPurchaseOrder() {
        return PurchaseOrder.builder()
                .number("A1000001BF")
                .approval(Approval.builder()
                        .status(PENDING)
                        .comments(Array.empty())
                        .confirmation(PurchaseOrder.Confirmation.builder()
                                .alias(UserAlias.of("bob"))
                                .updatedOn(LocalDateTime.now())
                                .build())
                        .build())
                .build();
    }
}
