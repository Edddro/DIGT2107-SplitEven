package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Add Expense input checks (GE-002, FR-008).
 *
 * <p>Each test names the field it expects to fail, because the acceptance
 * criterion is not just "rejected" but "rejected with a message next to the
 * field".</p>
 */
class ExpenseValidatorTest {

    private ExpenseValidator validator;
    private GroupModel group;
    private MemberModel adam;
    private MemberModel claire;

    @BeforeEach
    void setUp() {
        validator = new ExpenseValidator();
        group = new GroupModel("Apartment 4B");
        adam = new MemberModel("Adam");
        claire = new MemberModel("Claire");
        group.addMember(adam);
        group.addMember(claire);
    }

    /** A submission that passes every check, which each test then spoils one part of. */
    private ExpenseInput validInput() {
        return new ExpenseInput("40.00", adam, PaymentMethod.DEBIT, LocalDate.of(2026, 10, 18),
                "Groceries", "weekly shop", Arrays.asList(adam, claire));
    }

    @Test
    @DisplayName("TC-007: a complete expense passes validation")
    void tc007_acceptsACompleteExpense() {
        ValidationResult result = validator.validate(group, validInput());

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("TC-008: the expense note is optional")
    void tc008_acceptsAnExpenseWithoutANote() {
        ExpenseInput input = new ExpenseInput("40.00", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", null, Arrays.asList(adam, claire));

        assertTrue(validator.validate(group, input).isValid());
    }

    @ParameterizedTest(name = "TC-009: amount \"{0}\" is rejected")
    @ValueSource(strings = {"0", "0.00", "-1"})
    void tc009_rejectsZeroOrNegativeAmounts(String amountText) {
        ExpenseInput input = new ExpenseInput(amountText, adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.AMOUNT));
    }


    @Test
    @DisplayName("TC-010: amounts with fractions of a cent are rejected")
    void tc010_rejectsFractionsOfACent() {
        ExpenseInput input = new ExpenseInput("12.345", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.AMOUNT));
    }

    @Test
    @DisplayName("Supplementary: an amount too large to store is rejected")
    void rejectsAnAmountTooLargeToStore() {
        ExpenseInput input = new ExpenseInput("99999999999999999999", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertEquals("That amount is too large.", result.messageFor(ExpenseField.AMOUNT));
    }

    @Test
    @DisplayName("TC-011: a missing payment method is rejected")
    void tc011_rejectsAMissingPaymentMethod() {
        ExpenseInput input = new ExpenseInput("40.00", adam, null,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.PAYMENT_METHOD));
        assertNull(result.messageFor(ExpenseField.AMOUNT));
    }

    @Test
    @DisplayName("TC-012: a missing date is rejected")
    void tc012_rejectsAMissingDate() {
        ExpenseInput input = new ExpenseInput("40.00", adam, PaymentMethod.CASH,
                null, "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.DATE));
    }

    @Test
    @DisplayName("Supplementary: a missing category is rejected")
    void rejectsAMissingCategory() {
        ExpenseInput input = new ExpenseInput("40.00", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "   ", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.CATEGORY));
    }

    @Test
    @DisplayName("Supplementary: a missing payer is rejected")
    void rejectsAMissingPayer() {
        ExpenseInput input = new ExpenseInput("40.00", null, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.PAID_BY));
    }

    @Test
    @DisplayName("TC-013: an expense shared by nobody is rejected")
    void tc013_rejectsAnExpenseWithNoSharers() {
        ExpenseInput input = new ExpenseInput("40.00", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Collections.<MemberModel>emptyList());

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.SHARED_BY));
    }

    @Test
    @DisplayName("Supplementary: a payer outside the group is rejected")
    void rejectsAPayerWhoIsNotInTheGroup() {
        MemberModel stranger = new MemberModel("Stranger");
        ExpenseInput input = new ExpenseInput("40.00", stranger, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, claire));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.PAID_BY));
    }

    @Test
    @DisplayName("Supplementary: a sharer outside the group is rejected")
    void rejectsASharerWhoIsNotInTheGroup() {
        MemberModel stranger = new MemberModel("Stranger");
        ExpenseInput input = new ExpenseInput("40.00", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Arrays.asList(adam, stranger));

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertNotNull(result.messageFor(ExpenseField.SHARED_BY));
    }

    @Test
    @DisplayName("TC-018: an expense can have one sharer")
    void tc018_acceptsAnExpenseSharedByOneMember() {
        ExpenseInput input = new ExpenseInput("40.00", adam, PaymentMethod.CASH,
                LocalDate.of(2026, 10, 18), "Food", "", Collections.singletonList(adam));

        assertTrue(validator.validate(group, input).isValid());
    }

    @Test
    @DisplayName("Supplementary: every failed check is reported at once")
    void reportsEveryFailedCheckTogether() {
        ExpenseInput input = new ExpenseInput("0", null, null, null,
                "", "", new ArrayList<MemberModel>());

        ValidationResult result = validator.validate(group, input);

        assertFalse(result.isValid());
        assertEquals(6, result.getErrors().size());
        for (ExpenseField field : ExpenseField.values()) {
            assertNotNull(result.messageFor(field), "no message for " + field);
        }
    }

    @Test
    @DisplayName("Supplementary: validation errors cannot be modified")
    void errorsCannotBeModified() {
        ValidationResult result = validator.validate(group, validInput());
        List<ValidationError> errors = result.getErrors();

        try {
            errors.add(new ValidationError(ExpenseField.AMOUNT, "nope"));
            throw new AssertionError("the error list should not accept changes");
        } catch (UnsupportedOperationException expected) {
            // this is what we want
        }
    }
}
