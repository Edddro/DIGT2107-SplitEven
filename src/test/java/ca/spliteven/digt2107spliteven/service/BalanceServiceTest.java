package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import ca.spliteven.digt2107spliteven.model.PaymentModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests for working out each member's balance (BS-001, FR-011). */
class BalanceServiceTest {

    private MemberModel adam;
    private MemberModel edward;
    private MemberModel francis;
    private List<MemberModel> members;

    @BeforeEach
    void setUp() {
        adam = new MemberModel("Adam");
        edward = new MemberModel("Edward");
        francis = new MemberModel("Francis");
        members = Arrays.asList(adam, edward, francis);
    }

    private ExpenseModel expense(MemberModel paidBy, long amountCents, MemberModel... sharedBy) {
        return new ExpenseModel(amountCents, PaymentMethod.CASH, LocalDate.of(2026, 10, 18),
                "Food", "", paidBy, Arrays.asList(sharedBy));
    }

    private Map<MemberModel, Long> calculate(List<ExpenseModel> expenses,
            List<PaymentModel> payments) {
        return BalanceService.calculate(members, expenses, payments);
    }

    private long sum(Map<MemberModel, Long> balances) {
        return balances.values().stream().mapToLong(Long::longValue).sum();
    }

    @Test
    @DisplayName("BS-001: with no expenses everyone is at zero")
    void noExpensesMeansEveryoneIsSettled() {
        Map<MemberModel, Long> balances = calculate(Collections.emptyList(), Collections.emptyList());

        assertEquals(3, balances.size());
        assertEquals(0L, balances.get(adam));
        assertEquals(0L, balances.get(edward));
        assertEquals(0L, balances.get(francis));
    }

    @Test
    @DisplayName("BS-001: an equal split credits the payer and debits the others")
    void equalSplitBetweenAllMembers() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 3000L, adam, edward, francis)), Collections.emptyList());

        assertEquals(2000L, balances.get(adam));
        assertEquals(-1000L, balances.get(edward));
        assertEquals(-1000L, balances.get(francis));
    }

    @Test
    @DisplayName("BS-001: members who do not share an expense are not charged")
    void unevenSplitAmongSomeMembers() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 5000L, adam, edward)), Collections.emptyList());

        assertEquals(2500L, balances.get(adam));
        assertEquals(-2500L, balances.get(edward));
        assertEquals(0L, balances.get(francis));
    }

    @Test
    @DisplayName("BS-001: an amount that does not divide evenly still adds up to zero")
    void leftoverCentIsAssignedSoBalancesStayAtZero() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 1000L, adam, edward, francis)), Collections.emptyList());

        // 1000 / 3 leaves one cent over; Adam, first in the list, takes it.
        assertEquals(1000L - 334L, balances.get(adam));
        assertEquals(-333L, balances.get(edward));
        assertEquals(-333L, balances.get(francis));
        assertEquals(0L, sum(balances));
    }

    @Test
    @DisplayName("BS-001: a payer who does not share the expense is owed all of it")
    void payerNotSharingIsOwedTheFullAmount() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 4000L, edward, francis)), Collections.emptyList());

        assertEquals(4000L, balances.get(adam));
        assertEquals(-2000L, balances.get(edward));
        assertEquals(-2000L, balances.get(francis));
    }

    @Test
    @DisplayName("BS-001: balances combine across several expenses")
    void severalExpensesAreAddedTogether() {
        Map<MemberModel, Long> balances = calculate(Arrays.asList(
                expense(adam, 3000L, adam, edward, francis),
                expense(edward, 1200L, adam, edward)), Collections.emptyList());

        assertEquals(2000L - 600L, balances.get(adam));
        assertEquals(-1000L + 600L, balances.get(edward));
        assertEquals(-1000L, balances.get(francis));
        assertEquals(0L, sum(balances));
    }

    @Test
    @DisplayName("BS-001: a payment reduces what the payer owes and what the receiver is owed")
    void paymentReducesTheDebt() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 3000L, adam, edward, francis)),
                List.of(new PaymentModel(edward, adam, 400L)));

        assertEquals(1600L, balances.get(adam));
        assertEquals(-600L, balances.get(edward));
        assertEquals(-1000L, balances.get(francis));
        assertEquals(0L, sum(balances));
    }

    @Test
    @DisplayName("BS-001: paying the full debt settles both members")
    void fullPaymentSettlesTheDebt() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 2000L, adam, edward)),
                List.of(new PaymentModel(edward, adam, 1000L)));

        assertEquals(0L, balances.get(adam));
        assertEquals(0L, balances.get(edward));
    }

    @Test
    @DisplayName("BS-001: balances add up to zero across mixed expenses and payments")
    void balancesAlwaysAddUpToZero() {
        List<ExpenseModel> expenses = new ArrayList<>(Arrays.asList(
                expense(adam, 9999L, adam, edward, francis),
                expense(francis, 101L, adam, francis),
                expense(edward, 7L, adam, edward, francis)));
        List<PaymentModel> payments = List.of(
                new PaymentModel(francis, adam, 1234L),
                new PaymentModel(adam, edward, 50L));

        assertEquals(0L, sum(calculate(expenses, payments)));
    }

    @Test
    @DisplayName("BS-001: the service reads a group's balances from its saved expenses")
    void serviceUsesTheGroupsExpenses() {
        GroupService groupService = new GroupService();
        ExpenseService expenseService = new ExpenseService(groupService);
        GroupModel group = new GroupModel("Apartment");
        members.forEach(group::addMember);
        groupService.addGroup(group);

        ExpenseInput input = new ExpenseInput("30.00", adam, PaymentMethod.CASH,
                LocalDate.now(), "Food", "", Arrays.asList(adam, edward, francis));
        expenseService.add(group, input);

        Map<MemberModel, Long> balances = new BalanceService(expenseService).balancesFor(group);

        assertEquals(2000L, balances.get(adam));
        assertEquals(-1000L, balances.get(edward));
    }

    @Test
    @DisplayName("BS-001: a payment must have a positive amount between two different members")
    void invalidPaymentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PaymentModel(adam, edward, 0L));
        assertThrows(IllegalArgumentException.class, () -> new PaymentModel(adam, adam, 100L));
        assertThrows(IllegalArgumentException.class, () -> new PaymentModel(null, adam, 100L));
    }
}
