package ca.spliteven.digt2107spliteven.mock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MockOpeningData {

    private static final List<Debt> DEBTS = List.of(
            new Debt("Edward", "Adam", 1000),
            new Debt("Francis", "Adam", 1000),
            new Debt("Claire", "Adam", 1000));

    private MockOpeningData() {
    }

    public static List<Person> people() {
        Map<String, Integer> centsByName = totals();
        Map<String, List<String>> details = new LinkedHashMap<String, List<String>>();
        for (String name : centsByName.keySet()) {
            details.put(name, new ArrayList<String>());
        }
        for (Debt debt : DEBTS) {
            details.get(debt.from).add("owes " + debt.to + " " + money(debt.cents));
            details.get(debt.to).add("is owed by " + debt.from + " " + money(debt.cents));
        }
        List<Person> people = new ArrayList<Person>();
        for (Map.Entry<String, Integer> entry : centsByName.entrySet()) {
            if (entry.getValue() != 0) {
                people.add(new Person(entry.getKey(), signed(entry.getValue()), details.get(entry.getKey())));
            }
        }
        return people;
    }

    private static Map<String, Integer> totals() {
        Map<String, Integer> cents = new LinkedHashMap<String, Integer>();
        for (Debt debt : DEBTS) {
            cents.putIfAbsent(debt.to, 0);
            cents.putIfAbsent(debt.from, 0);
            cents.put(debt.to, cents.get(debt.to) + debt.cents);
            cents.put(debt.from, cents.get(debt.from) - debt.cents);
        }
        return cents;
    }

    private static String money(int cents) {
        return "$" + (cents / 100) + "." + String.format("%02d", cents % 100);
    }

    private static String signed(int cents) {
        if (cents < 0) {
            return "-" + money(-cents);
        }
        return money(cents);
    }

    public static final class Person {
        private final String name;
        private final String total;
        private final List<String> lines;

        private Person(String name, String total, List<String> lines) {
            this.name = name;
            this.total = total;
            this.lines = lines;
        }

        public String getName() {
            return name;
        }

        public String getTotal() {
            return total;
        }

        public List<String> getLines() {
            return lines;
        }
    }

    private static final class Debt {
        private final String from;
        private final String to;
        private final int cents;

        private Debt(String from, String to, int cents) {
            this.from = from;
            this.to = to;
            this.cents = cents;
        }
    }
}
