package ca.spliteven.digt2107spliteven.service;

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
import static org.junit.jupiter.api.Assertions.assertNull;

class ExpenseValidatorTest {

    private ExpenseService expenseService;
    private GroupModel group;
    private MemberModel ana;
    private MemberModel ben;
    private MemberModel cai;
    private List<MemberModel> members;

    @BeforeEach
    void setUp() {
        group = new GroupModel("Apartment 4B");
        GroupService groupService = new GroupService();
        groupService.addGroup(group);
        expenseService = new ExpenseService(groupService);
        ana = new MemberModel("Ana");
        ben = new MemberModel("Ben");
        cai = new MemberModel("Cai");
        members = Arrays.asList(ana, ben, cai);
        members.forEach(group::addMember);
    }

    private ExpenseInput validInput(String amount, PaymentMethod paymentMethod, LocalDate date,
            List<MemberModel> sharedBy) {
        return new ExpenseInput(amount, ana, paymentMethod, date, "Groceries", "No Frills run",
                sharedBy);
    }

    @Test
    @DisplayName("TC-011 Missing payment method is refused")
    void tc011_missingPaymentMethodIsRefused() {
        ValidationResult result = expenseService.add(group,
                validInput("25.00", null, LocalDate.of(2026, 10, 20), members));

        assertFalse(result.isValid());
        assertEquals("Choose a payment method",
                result.messageFor(ExpenseField.PAYMENT_METHOD));
        assertNull(result.messageFor(ExpenseField.AMOUNT));
        assertEquals(0, expenseService.countFor(group));
    }

    @Test
    @DisplayName("TC-012 Missing or impossible date is refused")
    void tc012_missingDateIsRefused() {
        ValidationResult result = expenseService.add(group,
                validInput("25.00", PaymentMethod.DEBIT, null, members));

        assertFalse(result.isValid());
        assertEquals("Enter a valid date", result.messageFor(ExpenseField.DATE));
        assertNull(result.messageFor(ExpenseField.AMOUNT));
        assertEquals(0, expenseService.countFor(group));
    }

    @Test
    @DisplayName("TC-013 No sharers is refused")
    void tc013_noSharersIsRefused() {
        ValidationResult result = expenseService.add(group,
                validInput("25.00", PaymentMethod.DEBIT, LocalDate.of(2026, 10, 20),
                        Collections.<MemberModel>emptyList()));

        assertFalse(result.isValid());
        assertEquals("Select at least one member", result.messageFor(ExpenseField.SHARED_BY));
        assertEquals(0, expenseService.countFor(group));
    }

    @Test
    @DisplayName("TC-015 Error messages on the Add Expense form")
    void tc015_errorMessagesAppearForOnlyInvalidFields() {
        ExpenseInput input = validInput("", null, LocalDate.of(2026, 10, 20), members);

        ValidationResult result = expenseService.add(group, input);

        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertEquals("Enter an amount", result.messageFor(ExpenseField.AMOUNT));
        assertEquals("Choose a payment method",
                result.messageFor(ExpenseField.PAYMENT_METHOD));
        assertNull(result.messageFor(ExpenseField.PAID_BY));
        assertNull(result.messageFor(ExpenseField.DATE));
        assertNull(result.messageFor(ExpenseField.CATEGORY));
        assertNull(result.messageFor(ExpenseField.SHARED_BY));
        assertEquals(0, expenseService.countFor(group));
    }
}
