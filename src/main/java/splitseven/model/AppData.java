package splitseven.model;

import java.util.ArrayList;
import java.util.List;

public record AppData(
        List<Group> groups,
        List<Expense> expenses,
        List<Payment> payments) {

    public static AppData empty() {
        return new AppData(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }
}
