package ca.spliteven.digt2107spliteven.service;

import ca.spliteven.digt2107spliteven.model.GroupModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupService {
    /** To hold each View of the app. */
    private final List<Observer> observers = new ArrayList<>();
    private final List<GroupModel> groups = new ArrayList<>();

    // This now adds MainView and potentially other Views such as GroupView, MemberView, etc.
    public void addObserver(Observer observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public void addGroup(GroupModel group) {
        groups.add(Objects.requireNonNull(group));
        notifyObservers();
    }

    public void addExpense() {
        // TODO
        notifyObservers();
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }
}
