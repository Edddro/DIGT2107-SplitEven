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
import java.util.Random;

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
    @DisplayName("Supplementary: with no expenses everyone is at zero")
    void noExpensesMeansEveryoneIsSettled() {
        Map<MemberModel, Long> balances = calculate(Collections.emptyList(), Collections.emptyList());

        assertEquals(3, balances.size());
        assertEquals(0L, balances.get(adam));
        assertEquals(0L, balances.get(edward));
        assertEquals(0L, balances.get(francis));
    }

    @Test
    @DisplayName("TC-023: balances reflect one equal-split expense")
    void tc023_equalSplitExpenseCalculatesBalances() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 3000L, adam, edward, francis)), Collections.emptyList());

        assertEquals(2000L, balances.get(adam));
        assertEquals(-1000L, balances.get(edward));
        assertEquals(-1000L, balances.get(francis));
    }

    @Test
    @DisplayName("Supplementary: members who do not share an expense are not charged")
    void unevenSplitAmongSomeMembers() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 5000L, adam, edward)), Collections.emptyList());

        assertEquals(2500L, balances.get(adam));
        assertEquals(-2500L, balances.get(edward));
        assertEquals(0L, balances.get(francis));
    }

    @Test
    @DisplayName("TC-017: equal split assigns leftover cents in member order")
    void tc017_equalSplit_leftoverCent() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 1000L, adam, edward, francis)), Collections.emptyList());

        // 1000 / 3 leaves one cent over; Adam, first in the list, takes it.
        assertEquals(1000L - 334L, balances.get(adam));
        assertEquals(-333L, balances.get(edward));
        assertEquals(-333L, balances.get(francis));
        assertEquals(0L, sum(balances));
    }

    @Test
    @DisplayName("TC-018: the only sharer owes their full share when the payer is not sharing")
    void tc018_singleSharerCanBeDifferentFromPayer() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 1200L, edward)), Collections.emptyList());

        assertEquals(1200L, balances.get(adam));
        assertEquals(-1200L, balances.get(edward));
        assertEquals(0L, balances.get(francis));
    }

    @Test
    @DisplayName("TC-024: balances combine expenses from several payers")
    void tc024_balancesWithSeveralPayers() {
        Map<MemberModel, Long> balances = calculate(Arrays.asList(
                expense(adam, 3000L, adam, edward, francis),
                expense(edward, 5000L, edward, francis),
                expense(francis, 1200L, adam, francis)), Collections.emptyList());

        assertEquals(1400L, balances.get(adam));
        assertEquals(1500L, balances.get(edward));
        assertEquals(-2900L, balances.get(francis));
        assertEquals(0L, sum(balances));
    }

    @Test
    @DisplayName("Supplementary: a payment reduces the debt")
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
    @DisplayName("Supplementary: a full payment settles both members")
    void fullPaymentSettlesTheDebt() {
        Map<MemberModel, Long> balances = calculate(
                List.of(expense(adam, 2000L, adam, edward)),
                List.of(new PaymentModel(edward, adam, 1000L)));

        assertEquals(0L, balances.get(adam));
        assertEquals(0L, balances.get(edward));
    }

    @Test
    @DisplayName("TC-025: balances add up to zero after each of 500 fixed-seed expenses")
    void tc025_balancesAlwaysAddUpToZero() {
        List<MemberModel> sixMembers = new ArrayList<>();
        for (int memberIndex = 0; memberIndex < 6; memberIndex++) {
            sixMembers.add(new MemberModel("Member " + memberIndex));
        }

        Random random = new Random(2107);
        List<ExpenseModel> expenses = new ArrayList<>();
        for (int expenseIndex = 0; expenseIndex < 500; expenseIndex++) {
            MemberModel payer = sixMembers.get(random.nextInt(sixMembers.size()));
            List<MemberModel> sharers = new ArrayList<>();
            for (MemberModel member : sixMembers) {
                if (random.nextBoolean()) {
                    sharers.add(member);
                }
            }
            if (sharers.isEmpty()) {
                sharers.add(sixMembers.get(random.nextInt(sixMembers.size())));
            }

            long amountCents = random.nextInt(50000) + 1;
            expenses.add(expense(payer, amountCents,
                    sharers.toArray(new MemberModel[0])));
            Map<MemberModel, Long> balances = BalanceService.calculate(
                    sixMembers, expenses, Collections.emptyList());
            assertEquals(0L, sum(balances), "balance after expense " + expenseIndex);
        }
    }

    @Test
    @DisplayName("TC-023: the service calculates balances from a group's expenses")
    void tc023_serviceUsesTheGroupsExpenses() {
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
    @DisplayName("Supplementary: a payment must be positive and between two different members")
    void invalidPaymentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new PaymentModel(adam, edward, 0L));
        assertThrows(IllegalArgumentException.class, () -> new PaymentModel(adam, adam, 100L));
        assertThrows(IllegalArgumentException.class, () -> new PaymentModel(null, adam, 100L));
    }
}
