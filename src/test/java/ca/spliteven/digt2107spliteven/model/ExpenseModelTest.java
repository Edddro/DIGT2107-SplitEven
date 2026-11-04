package ca.spliteven.digt2107spliteven.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the expense record itself.
 *
 */
class ExpenseModelTest {

    private MemberModel adam;
    private MemberModel claire;

    @BeforeEach
    void setUp() {
        adam = new MemberModel("Adam");
        claire = new MemberModel("Claire");
    }

    private ExpenseModel expense(long amountCents, List<MemberModel> sharedBy) {
        return new ExpenseModel(amountCents, PaymentMethod.CASH, LocalDate.of(2026, 10, 18),
                "Food", "dinner", adam, sharedBy);
    }

    @Test
    @DisplayName("TC-007: an expense preserves its details")
    void tc007_preservesExpenseDetails() {
        ExpenseModel saved = expense(4000L, Arrays.asList(adam, claire));

        assertEquals(4000L, saved.getAmountCents());
        assertEquals(PaymentMethod.CASH, saved.getPaymentMethod());
        assertEquals(LocalDate.of(2026, 10, 18), saved.getDate());
        assertEquals("Food", saved.getCategory());
        assertEquals("dinner", saved.getNote());
        assertEquals(adam, saved.getPaidBy());
        assertEquals(Arrays.asList(adam, claire), saved.getSharedBy());
    }

    @Test
    @DisplayName("TC-008: a missing note is saved as empty")
    void tc008_savesAnEmptyNoteWhenNoneIsGiven() {
        ExpenseModel saved = new ExpenseModel(4000L, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", null, adam, Collections.singletonList(adam));

        assertEquals("", saved.getNote());
    }

    @Test
    @DisplayName("Supplementary: expenses have unique IDs")
    void givesEveryExpenseItsOwnId() {
        ExpenseModel first = expense(1000L, Collections.singletonList(adam));
        ExpenseModel second = expense(1000L, Collections.singletonList(adam));

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    @DisplayName("Supplementary: changing the input list does not change the expense")
    void copiesTheListOfSharers() {
        List<MemberModel> sharedBy = new ArrayList<>(Arrays.asList(adam, claire));
        ExpenseModel saved = expense(4000L, sharedBy);

        sharedBy.clear();

        assertEquals(2, saved.getSharedBy().size());
    }

    @Test
    @DisplayName("Supplementary: saved sharers cannot be modified")
    void sharersCannotBeModified() {
        ExpenseModel saved = expense(4000L, Arrays.asList(adam, claire));

        assertThrows(UnsupportedOperationException.class, () -> saved.getSharedBy().clear());
    }

    @Test
    @DisplayName("Supplementary: invalid expense details are rejected")
    void refusesDetailsThatShouldNeverReachIt() {
        assertThrows(IllegalArgumentException.class,
                () -> expense(0L, Collections.singletonList(adam)));
        assertThrows(IllegalArgumentException.class,
                () -> expense(-1L, Collections.singletonList(adam)));
        assertThrows(IllegalArgumentException.class,
                () -> expense(4000L, Collections.<MemberModel>emptyList()));
        assertThrows(IllegalArgumentException.class,
                () -> new ExpenseModel(4000L, null, LocalDate.of(2026, 10, 18), "Food", "",
                        adam, Collections.singletonList(adam)));
        assertThrows(IllegalArgumentException.class,
                () -> new ExpenseModel(4000L, PaymentMethod.CASH, null, "Food", "",
                        adam, Collections.singletonList(adam)));
        assertThrows(IllegalArgumentException.class,
                () -> new ExpenseModel(4000L, PaymentMethod.CASH, LocalDate.of(2026, 10, 18), " ", "",
                        adam, Collections.singletonList(adam)));
        assertThrows(IllegalArgumentException.class,
                () -> new ExpenseModel(4000L, PaymentMethod.CASH, LocalDate.of(2026, 10, 18), "Food", "",
                        null, Collections.singletonList(adam)));
    }
}
