package splitseven.model;

import java.time.LocalDate;

public record Payment(
        String id,
        String groupId,
        String fromMemberId,
        String toMemberId,
        long amountCents,
        String paymentMethod,
        LocalDate date) {
}
