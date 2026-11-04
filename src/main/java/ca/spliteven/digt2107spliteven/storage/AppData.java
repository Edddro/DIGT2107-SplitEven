package ca.spliteven.digt2107spliteven.storage;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.PaymentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Snapshot of the existing application models that is read from or written to storage. */
public class AppData {
    private final List<GroupModel> groups = new ArrayList<>();
    private final Map<UUID, List<ExpenseModel>> expensesByGroup = new LinkedHashMap<>();
    private final Map<UUID, List<PaymentModel>> paymentsByGroup = new LinkedHashMap<>();

    public static AppData empty() {
        return new AppData();
    }

    public List<GroupModel> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public List<ExpenseModel> getExpenses(GroupModel group) {
        return Collections.unmodifiableList(expensesFor(group));
    }

    public List<PaymentModel> getPayments(GroupModel group) {
        return Collections.unmodifiableList(paymentsFor(group));
    }

    List<ExpenseModel> expensesFor(GroupModel group) {
        return expensesByGroup.computeIfAbsent(group.getId(), key -> new ArrayList<>());
    }

    List<PaymentModel> paymentsFor(GroupModel group) {
        return paymentsByGroup.computeIfAbsent(group.getId(), key -> new ArrayList<>());
    }

    List<ExpenseModel> expensesFor(UUID groupId) {
        return expensesByGroup.computeIfAbsent(groupId, key -> new ArrayList<>());
    }

    List<PaymentModel> paymentsFor(UUID groupId) {
        return paymentsByGroup.computeIfAbsent(groupId, key -> new ArrayList<>());
    }

    void addGroup(GroupModel group) {
        groups.add(group);
    }

    void removeGroup(GroupModel group) {
        groups.remove(group);
        expensesByGroup.remove(group.getId());
        paymentsByGroup.remove(group.getId());
    }
}
