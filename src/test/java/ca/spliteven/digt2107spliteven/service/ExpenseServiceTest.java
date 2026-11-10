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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseServiceTest {

    private GroupService groupService;
    private ExpenseService expenseService;
    private GroupModel group;
    private MemberModel ana;
    private MemberModel ben;
    private MemberModel cai;
    private List<MemberModel> members;

    @BeforeEach
    void setUp() {
        groupService = new GroupService();
        expenseService = new ExpenseService(groupService);
        group = new GroupModel("Apartment 4B");
        ana = new MemberModel("Ana");
        ben = new MemberModel("Ben");
        cai = new MemberModel("Cai");
        members = Arrays.asList(ana, ben, cai);
        members.forEach(group::addMember);
        groupService.addGroup(group);
    }

    private ExpenseInput standardExpense(String amount, String note, List<MemberModel> sharedBy) {
        return new ExpenseInput(amount, ana, PaymentMethod.DEBIT,
                LocalDate.of(2026, 10, 20), "Groceries", note, sharedBy);
    }

    @Test
    @DisplayName("TC-007 Save an expense with every detail")
    void tc007_saveExpenseWithEveryDetail() {
        ExpenseInput input = standardExpense("45.00", "No Frills run", members);

        assertTrue(expenseService.add(group, input).isValid());

        List<ExpenseModel> expenses = expenseService.expensesFor(group);
        assertEquals(1, expenses.size());
        ExpenseModel saved = expenses.get(0);
        assertEquals(4500L, saved.getAmountCents());
        assertEquals(ana, saved.getPaidBy());
        assertEquals(PaymentMethod.DEBIT, saved.getPaymentMethod());
        assertEquals(LocalDate.of(2026, 10, 20), saved.getDate());
        assertEquals("Groceries", saved.getCategory());
        assertEquals("No Frills run", saved.getNote());
        assertEquals(members, saved.getSharedBy());
    }

    @Test
    @DisplayName("TC-008 The note is optional")
    void tc008_noteIsOptional() {
        assertTrue(expenseService.add(group, standardExpense("45.00", null, members)).isValid());

        assertEquals("", expenseService.expensesFor(group).get(0).getNote());
    }

    @Test
    @DisplayName("TC-009 Amount of zero or less is refused")
    void tc009_amountOfZeroOrLessIsRefused() {
        for (String amount : Arrays.asList("0.00", "-5.00")) {
            ValidationResult result = expenseService.add(
                    group, standardExpense(amount, "No Frills run", members));

            assertFalse(result.isValid());
            assertEquals("Amount must be greater than $0.00",
                    result.messageFor(ExpenseField.AMOUNT));
            assertEquals(0, expenseService.countFor(group));
        }
    }

    @Test
    @DisplayName("TC-010 Amount must be in whole cents")
    void tc010_amountMustBeInWholeCents() {
        ValidationResult invalidResult = expenseService.add(
                group, standardExpense("12.345", "No Frills run", members));

        assertFalse(invalidResult.isValid());
        assertEquals("Use at most two decimal places",
                invalidResult.messageFor(ExpenseField.AMOUNT));
        assertEquals(0, expenseService.countFor(group));

        ExpenseInput oneCentExpense = standardExpense("0.01", "", Arrays.asList(ana));
        assertTrue(expenseService.add(group, oneCentExpense).isValid());
        assertEquals(1L, expenseService.expensesFor(group).get(0).getAmountCents());
    }

    @Test
    @DisplayName("TC-014 A refused expense changes nothing")
    void tc014_refusedExpenseChangesNothing() {
        expenseService.add(group, standardExpense("30.00", "", members));
        BalanceService balanceService = new BalanceService(expenseService);
        Map<MemberModel, Long> balancesBefore = balanceService.balancesFor(group);

        ValidationResult result = expenseService.add(
                group, standardExpense("0.00", "", members));

        assertFalse(result.isValid());
        assertEquals(1, expenseService.countFor(group));
        assertEquals(balancesBefore, balanceService.balancesFor(group));
        assertEquals(2000L, balancesBefore.get(ana));
        assertEquals(-1000L, balancesBefore.get(ben));
        assertEquals(-1000L, balancesBefore.get(cai));
    }
}
