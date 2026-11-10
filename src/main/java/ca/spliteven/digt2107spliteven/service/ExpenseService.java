package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;

import java.util.List;
import java.util.Objects;

/** Records expenses against a group and retrieves its saved expense history. */
public class ExpenseService {
    private final GroupService groupService;
    private final ExpenseValidator validator;
    // TODO This expensesByGroup should probably be tied to the actual GroupModel like how the MemberModel is.
    // Keyed by the group object itself rather than its id, so this map does not
    // depend on how GroupModel chooses to identify a group.
    private final Map<GroupModel, List<ExpenseModel>> expensesByGroup = new IdentityHashMap<>();

    public ExpenseService(GroupService groupService) {
        this(groupService, new ExpenseValidator());
    }

    public ExpenseService(GroupService groupService, ExpenseValidator validator) {
        this.groupService = Objects.requireNonNull(groupService);
        this.validator = Objects.requireNonNull(validator);
    }

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
        // eventually this will likely need to be more robust to handle editing/deleting expenses
        group.addExpenses(expense);
        groupService.updateGroup(group);
        return result;
    }

    /** Every expense recorded against a group, oldest first. */
    public List<ExpenseModel> expensesFor(GroupModel group) {
        Objects.requireNonNull(group, "group");
        return groupService.expensesFor(group);
    }

    public int countFor(GroupModel group) {
        return expensesFor(group).size();
    }
}
