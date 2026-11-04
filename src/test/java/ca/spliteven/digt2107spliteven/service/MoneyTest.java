package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTest {

    @Test
    @DisplayName("TC-029 Money is stored in whole cents")
    void tc029_moneyIsStoredInWholeCents() {
        assertEquals(1999L, Money.parseToCents("19.99"));
        assertEquals(10L, Money.parseToCents("0.1"));
        assertEquals(100000L, Money.parseToCents("1000"));

        List<MemberModel> members = new ArrayList<>();
        for (int memberIndex = 0; memberIndex < 6; memberIndex++) {
            members.add(new MemberModel("Member " + memberIndex));
        }

        Random random = new Random(2107);
        for (int splitIndex = 0; splitIndex < 10000; splitIndex++) {
            long amountCents = random.nextInt(50000) + 1;
            int sharerCount = random.nextInt(members.size()) + 1;
            List<MemberModel> sharers = members.subList(0, sharerCount);
            ExpenseModel expense = new ExpenseModel(amountCents, PaymentMethod.DEBIT,
                    LocalDate.of(2026, 10, 20), "Groceries", "", members.get(0), sharers);
            Map<MemberModel, Long> shares = expense.getShares();

            assertEquals(amountCents,
                    shares.values().stream().mapToLong(Long::longValue).sum(),
                    "split " + splitIndex);
        }
    }
}
