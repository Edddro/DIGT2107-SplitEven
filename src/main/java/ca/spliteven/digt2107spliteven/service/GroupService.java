package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentModel;
import ca.spliteven.digt2107spliteven.storage.AppDataRepository;
import ca.spliteven.digt2107spliteven.storage.Storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupService {
    private final List<Observer> observers = new ArrayList<>();
    private final AppDataRepository dataRepository;

    /** Creates an in-memory service for tests that do not exercise persistence. */
    public GroupService() {
        this.dataRepository = new AppDataRepository();
    }

    public GroupService(Storage storage) throws IOException {
        this.dataRepository = new AppDataRepository(storage);
    }

    public void addObserver(Observer observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public void addGroup(GroupModel group) {
        try {
            dataRepository.addGroup(Objects.requireNonNull(group));
        } catch (IOException error) {
            throw new UncheckedIOException("Could not save group", error);
        }
        notifyObservers();
    }

    public void addMember(GroupModel group, MemberModel member) {
        try {
            dataRepository.addMember(group, member);
        } catch (IOException error) {
            throw new UncheckedIOException("Could not save group member", error);
        }
        notifyObservers();
    }

    void addExpense(GroupModel group, ExpenseModel expense) {
        try {
            dataRepository.addExpense(group, expense);
        } catch (IOException error) {
            throw new UncheckedIOException("Could not save expense", error);
        }
        notifyObservers();
    }

    public void addPayment(GroupModel group, PaymentModel payment) {
        try {
            dataRepository.addPayment(group, payment);
        } catch (IOException error) {
            throw new UncheckedIOException("Could not save payment", error);
        }
        notifyObservers();
    }

    public List<GroupModel> getGroups() {
        return dataRepository.getGroups();
    }

    List<ExpenseModel> expensesFor(GroupModel group) {
        return dataRepository.getExpenses(group);
    }

    List<PaymentModel> paymentsFor(GroupModel group) {
        return dataRepository.getPayments(group);
    }

    public List<PaymentModel> getPayments(GroupModel group) {
        return dataRepository.getPayments(group);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
