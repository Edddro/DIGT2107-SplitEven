package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class BalanceServiceTest {

    private MemberModel ana;
    private MemberModel ben;
    private MemberModel cai;
    private List<MemberModel> members;

    @BeforeEach
    void setUp() {
        ana = new MemberModel("Ana");
        ben = new MemberModel("Ben");
        cai = new MemberModel("Cai");
        members = Arrays.asList(ana, ben, cai);
    }

    private ExpenseModel expense(MemberModel paidBy, long amountCents, MemberModel... sharedBy) {
        return new ExpenseModel(amountCents, PaymentMethod.DEBIT,
                LocalDate.of(2026, 10, 20), "Groceries", "No Frills run",
                paidBy, Arrays.asList(sharedBy));
    }

    private Map<MemberModel, Long> calculate(List<MemberModel> groupMembers,
            List<ExpenseModel> expenses) {
        return BalanceService.calculate(groupMembers, expenses, Collections.emptyList());
    }

    private long sum(Map<MemberModel, Long> balances) {
        return balances.values().stream().mapToLong(Long::longValue).sum();
    }

    @Test
    @DisplayName("TC-016 Equal split, even amount")
    void tc016_equalSplit_evenAmount() {
        ExpenseModel expense = expense(ana, 3000L, ana, ben, cai);

        assertEquals(1000L, expense.getShares().get(ana));
        assertEquals(1000L, expense.getShares().get(ben));
        assertEquals(1000L, expense.getShares().get(cai));
    }

    @Test
    @DisplayName("TC-017 Equal split with a leftover cent")
    void tc017_equalSplit_leftoverCent() {
        ExpenseModel expense = expense(ana, 1000L, ana, ben, cai);

        assertEquals(334L, expense.getShares().get(ana));
        assertEquals(333L, expense.getShares().get(ben));
        assertEquals(333L, expense.getShares().get(cai));
    }

    @Test
    @DisplayName("TC-018 Equal split with one sharer")
    void tc018_equalSplit_oneSharer() {
        ExpenseModel expense = expense(ana, 1200L, ben);

        assertEquals(Collections.singletonMap(ben, 1200L), expense.getShares());
    }

    @Test
    @DisplayName("TC-023 Balance from one expense")
    void tc023_balanceFromOneExpense() {
        Map<MemberModel, Long> balances = calculate(members,
                Collections.singletonList(expense(ana, 3000L, ana, ben, cai)));

        assertEquals(2000L, balances.get(ana));
        assertEquals(-1000L, balances.get(ben));
        assertEquals(-1000L, balances.get(cai));
    }

    @Test
    @DisplayName("TC-024 Balances with several payers")
    void tc024_balancesWithSeveralPayers() {
        List<ExpenseModel> expenses = Arrays.asList(
                expense(ana, 3000L, ana, ben, cai),
                expense(ben, 5000L, ben, cai),
                expense(cai, 1200L, ana, cai));

        Map<MemberModel, Long> balances = calculate(members, expenses);

        assertEquals(1400L, balances.get(ana));
        assertEquals(1500L, balances.get(ben));
        assertEquals(-2900L, balances.get(cai));
    }

    @Test
    @DisplayName("TC-025 Balances always add up to zero")
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
            assertEquals(0L, sum(calculate(sixMembers, expenses)),
                    "balance after expense " + expenseIndex);
        }
    }

    @Test
    @DisplayName("TC-027 Balances update within 1 second")
    void tc027_balancesUpdateWithinOneSecond() {
        List<MemberModel> twentyMembers = new ArrayList<>();
        for (int memberIndex = 0; memberIndex < 20; memberIndex++) {
            twentyMembers.add(new MemberModel("Member " + memberIndex));
        }

        Random random = new Random(2107);
        List<ExpenseModel> expenses = new ArrayList<>();
        for (int expenseIndex = 0; expenseIndex < 1000; expenseIndex++) {
            MemberModel payer = twentyMembers.get(random.nextInt(twentyMembers.size()));
            MemberModel sharer = twentyMembers.get(random.nextInt(twentyMembers.size()));
            expenses.add(expense(payer, random.nextInt(50000) + 1, sharer));
        }

        for (int run = 0; run < 20; run++) {
            long startTime = System.nanoTime();
            expenses.add(expense(twentyMembers.get(run), 1000L, twentyMembers.get((run + 1) % 20)));
            BalanceService.calculate(twentyMembers, expenses, Collections.emptyList());
            long elapsedNanos = System.nanoTime() - startTime;

            assertTrue(elapsedNanos < 1_000_000_000L,
                    "balance update " + (run + 1) + " took " + elapsedNanos + " ns");
        }
    }
}
