package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks an Add Expense submission before anything is saved.
 *
 * The rules:
 * 
 * the amount is greater than zero, and is money this app can store exactly
 * 
 * a payment method is chosen
 * the date is filled in
 * at least one member shares the expense
 * a payer is chosen, and both the payer and the sharers belong to the
 * group
 * a category is chosen, which RS-002 later totals spending by
 * 
 */
public class ExpenseValidator {

    /**
     * Checks one submission against the group it would be saved into.
     *
     * @return every failed check, or an empty result when the expense is good to
     *         save
     */
    public ValidationResult validate(GroupModel group, ExpenseInput input) {
        List<ValidationError> errors = new ArrayList<>();

        checkAmount(input, errors);
        checkPaidBy(group, input, errors);
        checkPaymentMethod(input, errors);
        checkDate(input, errors);
        checkCategory(input, errors);
        checkSharedBy(group, input, errors);

        return new ValidationResult(errors);
    }

    private void checkAmount(ExpenseInput input, List<ValidationError> errors) {
        String amountText = input.getAmountText();
        if (amountText == null || amountText.trim().isEmpty()) {
            errors.add(new ValidationError(ExpenseField.AMOUNT, "Enter an amount."));
            return;
        }
        if (!Money.isValidAmount(amountText)) {
            errors.add(new ValidationError(ExpenseField.AMOUNT,
                    "Enter an amount like 12.34, using at most two decimal places."));
            return;
        }
        long cents;
        try {
            cents = Money.parseToCents(amountText);
        } catch (ArithmeticException tooBig) {
            errors.add(new ValidationError(ExpenseField.AMOUNT, "That amount is too large."));
            return;
        }
        if (cents <= 0) {
            errors.add(new ValidationError(ExpenseField.AMOUNT, "Amount must be greater than zero."));
        }
    }

    private void checkPaidBy(GroupModel group, ExpenseInput input, List<ValidationError> errors) {
        MemberModel paidBy = input.getPaidBy();
        if (paidBy == null) {
            errors.add(new ValidationError(ExpenseField.PAID_BY, "Choose who paid."));
        } else if (!isInGroup(group, paidBy)) {
            errors.add(new ValidationError(ExpenseField.PAID_BY,
                    "That person is not a member of this group."));
        }
    }

    private void checkPaymentMethod(ExpenseInput input, List<ValidationError> errors) {
        if (input.getPaymentMethod() == null) {
            errors.add(new ValidationError(ExpenseField.PAYMENT_METHOD, "Choose a payment method."));
        }
    }

    private void checkDate(ExpenseInput input, List<ValidationError> errors) {
        if (input.getDate() == null) {
            errors.add(new ValidationError(ExpenseField.DATE, "Enter a valid date."));
        }
    }

    private void checkCategory(ExpenseInput input, List<ValidationError> errors) {
        String category = input.getCategory();
        if (category == null || category.trim().isEmpty()) {
            errors.add(new ValidationError(ExpenseField.CATEGORY, "Choose a category."));
        }
    }

    private void checkSharedBy(GroupModel group, ExpenseInput input, List<ValidationError> errors) {
        List<MemberModel> sharedBy = input.getSharedBy();
        if (sharedBy == null || sharedBy.isEmpty()) {
            errors.add(new ValidationError(ExpenseField.SHARED_BY,
                    "Choose at least one member who shares this expense."));
            return;
        }
        for (MemberModel member : sharedBy) {
            if (!isInGroup(group, member)) {
                errors.add(new ValidationError(ExpenseField.SHARED_BY,
                        "Everyone sharing the expense must be a member of this group."));
                return;
            }
        }
    }

    private boolean isInGroup(GroupModel group, MemberModel member) {
        if (group == null) {
            return false;
        }
        for (MemberModel candidate : group.getMembers()) {
            if (candidate.getId().equals(member.getId())) {
                return true;
            }
        }
        return false;
    }
}
