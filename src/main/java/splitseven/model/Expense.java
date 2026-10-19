package splitseven.model;

import java.time.LocalDate;
import java.util.Map;

/** An expense with the amount and each share stored in whole cents. */
public record Expense(
        String id,
        String groupId,
        String payerId,
        long amountCents,
        String paymentMethod,
        LocalDate date,
        String category,
        String note,
        Map<String, Long> shares) {
}
