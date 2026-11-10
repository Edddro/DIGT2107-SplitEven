package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.service.ExpenseService;
import ca.spliteven.digt2107spliteven.service.ValidationResult;

import java.io.UncheckedIOException;

/**
 * Wires the Add Expense form to the expense service.
 *
 * 
 */
public class AddExpenseController {

    private final ExpenseService expenseService;
    private final AddExpenseView view;
    private final GroupModel group;

    public AddExpenseController(ExpenseService expenseService,
            AddExpenseView view,
            Navigator navigator,
            GroupModel group) {
        this.expenseService = expenseService;
        this.view = view;
        this.group = group;

        view.setHistory(expenseService.expensesFor(group));

        view.getSaveButton().setOnAction(event -> save());
        view.getCancelButton().setOnAction(event -> navigator.showMain());
    }

    private void save() {
        view.clearMessages();

        final ValidationResult result;
        try {
            result = expenseService.add(group, view.readValues());
        } catch (UncheckedIOException error) {
            view.showSaveError("Could not save expense: " + error.getCause().getMessage());
            return;
        }
        if (!result.isValid()) {
            view.showErrors(result);
            return;
        }

        view.setHistory(expenseService.expensesFor(group));
        view.clearForm();
        view.showConfirmation("Expense saved.");
    }
}
