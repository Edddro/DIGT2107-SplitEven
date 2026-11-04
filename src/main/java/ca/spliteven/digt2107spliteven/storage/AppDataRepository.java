package ca.spliteven.digt2107spliteven.storage;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentModel;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/** Loads the application models and persists each confirmed change. */
public class AppDataRepository {
    private final Storage storage;
    private final AppData appData;

    /** Creates an in-memory repository for tests that do not exercise persistence. */
    public AppDataRepository() {
        this.storage = null;
        this.appData = AppData.empty();
    }

    public AppDataRepository(Storage storage) throws IOException {
        this.storage = Objects.requireNonNull(storage);
        this.appData = storage.load();
    }

    public List<GroupModel> getGroups() {
        return appData.getGroups();
    }

    public List<ExpenseModel> getExpenses(GroupModel group) {
        return appData.getExpenses(group);
    }

    public List<PaymentModel> getPayments(GroupModel group) {
        return appData.getPayments(group);
    }

    public void addGroup(GroupModel group) throws IOException {
        appData.addGroup(Objects.requireNonNull(group));
        saveOrRollback(() -> appData.removeGroup(group));
    }

    public void addMember(GroupModel group, MemberModel member) throws IOException {
        requireKnownGroup(group);
        group.addMember(Objects.requireNonNull(member));
        saveOrRollback(() -> group.getMembers().remove(member));
    }

    public void addExpense(GroupModel group, ExpenseModel expense) throws IOException {
        requireKnownGroup(group);
        List<ExpenseModel> expenses = appData.expensesFor(group);
        expenses.add(Objects.requireNonNull(expense));
        saveOrRollback(() -> expenses.remove(expense));
    }

    public void addPayment(GroupModel group, PaymentModel payment) throws IOException {
        requireKnownGroup(group);
        List<PaymentModel> payments = appData.paymentsFor(group);
        payments.add(Objects.requireNonNull(payment));
        saveOrRollback(() -> payments.remove(payment));
    }

    private void saveOrRollback(Runnable rollback) throws IOException {
        if (storage == null) {
            return;
        }
        try {
            storage.save(appData);
        } catch (IOException error) {
            rollback.run();
            throw error;
        }
    }

    private void requireKnownGroup(GroupModel group) {
        if (!appData.getGroups().contains(Objects.requireNonNull(group))) {
            throw new IllegalArgumentException("Group is not loaded by this controller");
        }
    }
}
