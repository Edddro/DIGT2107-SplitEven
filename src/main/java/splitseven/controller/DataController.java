package splitseven.controller;

import splitseven.model.AppData;
import splitseven.model.Expense;
import splitseven.model.Group;
import splitseven.model.Member;
import splitseven.model.Payment;
import splitseven.storage.Storage;

import java.io.IOException;
import java.util.List;

/** Loads application data on startup and saves it after each change. */
public class DataController {
    private final Storage storage;
    private final AppData appData;

    public DataController(Storage storage) throws IOException {
        this.storage = storage;
        this.appData = storage.load();
    }

    public List<Group> getGroups() {
        return appData.groups();
    }

    public List<Expense> getExpenses() {
        return appData.expenses();
    }

    public List<Payment> getPayments() {
        return appData.payments();
    }

    public Group addGroup(String groupName) throws IOException {
        Group newGroup = Group.create(groupName);
        appData.groups().add(newGroup);
        storage.save(appData);
        return newGroup;
    }

    public Member addMember(Group group, String memberName) throws IOException {
        Member newMember = Member.create(memberName);
        group.addMember(newMember);
        storage.save(appData);
        return newMember;
    }

    public void addExpense(Expense expense) throws IOException {
        appData.expenses().add(expense);
        storage.save(appData);
    }

    public void addPayment(Payment payment) throws IOException {
        appData.payments().add(payment);
        storage.save(appData);
    }
}
