package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Records expenses against a group .
 * 
 */
public class ExpenseService {

    private final GroupService groupService;
    private final ExpenseValidator validator;
    // Keyed by the group object rather than its id: GroupModel hands out ids from
    // the current millisecond, so two groups made in quick succession can share
    // one.
    private final Map<GroupModel, List<ExpenseModel>> expensesByGroup = new IdentityHashMap<>();

    public ExpenseService(GroupService groupService) {
        this(groupService, new ExpenseValidator());
    }

    public ExpenseService(GroupService groupService, ExpenseValidator validator) {
        this.groupService = Objects.requireNonNull(groupService);
        this.validator = Objects.requireNonNull(validator);
    }

    /**
     * Checks a submission and, if it passes, saves it against the group.
     *
     * @return the result of the checks; when it is valid the expense has been saved
     */
    public ValidationResult add(GroupModel group, ExpenseInput input) {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(input, "input");

        ValidationResult result = validator.validate(group, input);
        if (!result.isValid()) {
            return result;
        }

        ExpenseModel expense = new ExpenseModel(
                Money.parseToCents(input.getAmountText()),
                input.getPaymentMethod(),
                input.getDate(),
                input.getCategory(),
                input.getNote(),
                input.getPaidBy(),
                input.getSharedBy());

        List<ExpenseModel> expenses = expensesByGroup.get(group);
        if (expenses == null) {
            expenses = new ArrayList<>();
            expensesByGroup.put(group, expenses);
        }
        expenses.add(expense);

        groupService.notifyObservers();
        return result;
    }

    /**
     * Every expense recorded against a group, oldest first. The list cannot be
     * modified.
     */
    public List<ExpenseModel> expensesFor(GroupModel group) {
        Objects.requireNonNull(group, "group");
        List<ExpenseModel> expenses = expensesByGroup.get(group);
        if (expenses == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(expenses));
    }

    /**
     * How many expenses a group has, which saves callers building the whole list.
     */
    public int countFor(GroupModel group) {
        return expensesFor(group).size();
    }
}
