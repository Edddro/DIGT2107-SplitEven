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
 * Tests for the expense record itself (GE-002, FR-004).
 *
 * <p>The checks here are the safety net behind
 * {@code ExpenseValidator}: by the time an expense is built the input has already
 * been checked, so anything caught here is a bug in our own code rather than
 * something a user typed.</p>
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
    @DisplayName("TC-029: an expense holds exactly what it was given")
    void holdsItsDetails() {
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
    @DisplayName("TC-030: a missing note becomes an empty string, never null")
    void turnsAMissingNoteIntoAnEmptyString() {
        ExpenseModel saved = new ExpenseModel(4000L, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", null, adam, Collections.singletonList(adam));

        assertEquals("", saved.getNote());
    }

    @Test
    @DisplayName("TC-031: two expenses never share an id")
    void givesEveryExpenseItsOwnId() {
        ExpenseModel first = expense(1000L, Collections.singletonList(adam));
        ExpenseModel second = expense(1000L, Collections.singletonList(adam));

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    @DisplayName("TC-032: changing the list passed in does not change the saved expense")
    void copiesTheListOfSharers() {
        List<MemberModel> sharedBy = new ArrayList<>(Arrays.asList(adam, claire));
        ExpenseModel saved = expense(4000L, sharedBy);

        sharedBy.clear();

        assertEquals(2, saved.getSharedBy().size());
    }

    @Test
    @DisplayName("TC-033: the saved list of sharers cannot be modified")
    void sharersCannotBeModified() {
        ExpenseModel saved = expense(4000L, Arrays.asList(adam, claire));

        assertThrows(UnsupportedOperationException.class, () -> saved.getSharedBy().clear());
    }

    @Test
    @DisplayName("TC-034: an expense cannot be built from details the checks would have blocked")
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
