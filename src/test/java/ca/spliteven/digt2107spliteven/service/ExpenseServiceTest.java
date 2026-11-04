package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for recording an expense against a group (GE-002, FR-004). */
class ExpenseServiceTest {

    /** Stands in for a screen, so we can tell whether it would have refreshed. */
    private static class CountingObserver implements Observer {
        private int updates;

        @Override
        public void update(GroupService model) {
            updates++;
        }
    }

    private GroupService groupService;
    private ExpenseService expenseService;
    private CountingObserver observer;
    private GroupModel group;
    private MemberModel adam;
    private MemberModel claire;

    @BeforeEach
    void setUp() {
        groupService = new GroupService();
        expenseService = new ExpenseService(groupService);

        group = new GroupModel("Apartment 4B");
        adam = new MemberModel("Adam");
        claire = new MemberModel("Claire");
        group.addMember(adam);
        group.addMember(claire);
        groupService.addGroup(group);

        // Registered after the group is in place, so the count starts at zero.
        observer = new CountingObserver();
        groupService.addObserver(observer);
    }

    private ExpenseInput validInput(String amount) {
        return new ExpenseInput(amount, adam, PaymentMethod.DEBIT, LocalDate.of(2026, 10, 18),
                "Groceries", "weekly shop", Arrays.asList(adam, claire));
    }

    @Test
    @DisplayName("TC-007: a valid expense is saved with its details")
    void tc007_savesAValidExpenseWithItsDetails() {
        ValidationResult result = expenseService.add(group, validInput("40.00"));

        assertTrue(result.isValid());

        List<ExpenseModel> history = expenseService.expensesFor(group);
        assertEquals(1, history.size());

        ExpenseModel saved = history.get(0);
        assertEquals(4000L, saved.getAmountCents());
        assertEquals(PaymentMethod.DEBIT, saved.getPaymentMethod());
        assertEquals(LocalDate.of(2026, 10, 18), saved.getDate());
        assertEquals("Groceries", saved.getCategory());
        assertEquals("weekly shop", saved.getNote());
        assertSame(adam, saved.getPaidBy());
        assertEquals(Arrays.asList(adam, claire), saved.getSharedBy());
    }

    @Test
    @DisplayName("TC-026: saving an expense refreshes observers")
    void tc026_notifiesObserversAfterASave() {
        expenseService.add(group, validInput("40.00"));

        assertEquals(1, observer.updates);
    }

    @Test
    @DisplayName("TC-014: a refused expense changes nothing")
    void tc014_savesNothingWhenTheInputIsInvalid() {
        ValidationResult result = expenseService.add(group,
                new ExpenseInput("0", null, null, null, "", "", Collections.<MemberModel>emptyList()));

        assertFalse(result.isValid());
        assertEquals(0, expenseService.countFor(group));
        assertEquals(0, observer.updates);
    }

    @Test
    @DisplayName("Supplementary: expenses are kept in insertion order")
    void keepsExpensesInOrder() {
        expenseService.add(group, validInput("10.00"));
        expenseService.add(group, validInput("20.00"));
        expenseService.add(group, validInput("30.00"));

        List<ExpenseModel> history = expenseService.expensesFor(group);

        assertEquals(3, history.size());
        assertEquals(1000L, history.get(0).getAmountCents());
        assertEquals(2000L, history.get(1).getAmountCents());
        assertEquals(3000L, history.get(2).getAmountCents());
    }

    @Test
    @DisplayName("Supplementary: expenses are kept separate by group")
    void keepsGroupsApart() {
        GroupModel trip = new GroupModel("Montreal trip");
        MemberModel edward = new MemberModel("Edward");
        trip.addMember(edward);
        groupService.addGroup(trip);

        expenseService.add(group, validInput("40.00"));
        expenseService.add(trip, new ExpenseInput("15.00", edward, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Transport", "", Collections.singletonList(edward)));

        assertEquals(1, expenseService.countFor(group));
        assertEquals(1, expenseService.countFor(trip));
        assertEquals(4000L, expenseService.expensesFor(group).get(0).getAmountCents());
        assertEquals(1500L, expenseService.expensesFor(trip).get(0).getAmountCents());
    }

    @Test
    @DisplayName("Supplementary: a new group has an empty expense history")
    void returnsAnEmptyHistoryForANewGroup() {
        GroupModel empty = new GroupModel("Book club");

        assertTrue(expenseService.expensesFor(empty).isEmpty());
        assertEquals(0, expenseService.countFor(empty));
    }

    @Test
    @DisplayName("Supplementary: callers cannot modify expense history")
    void historyCannotBeModified() {
        expenseService.add(group, validInput("40.00"));

        List<ExpenseModel> history = expenseService.expensesFor(group);

        assertThrows(UnsupportedOperationException.class, () -> history.remove(0));
        assertEquals(1, expenseService.countFor(group));
    }

    @Test
    @DisplayName("Supplementary: null group or submission is rejected")
    void rejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> expenseService.add(null, validInput("40.00")));
        assertThrows(NullPointerException.class, () -> expenseService.add(group, null));
    }
}
