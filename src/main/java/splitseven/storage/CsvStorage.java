package splitseven.storage;

import splitseven.model.AppData;
import splitseven.model.Expense;
import splitseven.model.Group;
import splitseven.model.Member;
import splitseven.model.Payment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Stores groups, members, expenses, and payments in separate local CSV files. */
public class CsvStorage implements Storage {
    private static final List<String> GROUP_HEADER = List.of("id", "name");
    private static final List<String> MEMBER_HEADER = List.of("id", "groupId", "name");
    private static final List<String> EXPENSE_HEADER = List.of(
            "id", "groupId", "payerId", "amountCents", "paymentMethod",
            "date", "category", "note", "shares");
    private static final List<String> PAYMENT_HEADER = List.of(
            "id", "groupId", "fromMemberId", "toMemberId", "amountCents",
            "paymentMethod", "date");

    private final Path dataDirectory;

    public CsvStorage(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void save(AppData appData) throws IOException {
        Files.createDirectories(dataDirectory);

        List<List<String>> groupRows = new ArrayList<>();
        List<List<String>> memberRows = new ArrayList<>();
        for (Group group : appData.groups()) {
            groupRows.add(List.of(group.getId(), group.getName()));

            for (Member member : group.getMembers()) {
                memberRows.add(List.of(member.id(), group.getId(), member.name()));
            }
        }

        List<List<String>> expenseRows = new ArrayList<>();
        for (Expense expense : appData.expenses()) {
            expenseRows.add(List.of(
                    expense.id(),
                    expense.groupId(),
                    expense.payerId(),
                    Long.toString(expense.amountCents()),
                    expense.paymentMethod(),
                    expense.date().toString(),
                    expense.category(),
                    expense.note() == null ? "" : expense.note(),
                    encodeShares(expense.shares())));
        }

        List<List<String>> paymentRows = new ArrayList<>();
        for (Payment payment : appData.payments()) {
            paymentRows.add(List.of(
                    payment.id(),
                    payment.groupId(),
                    payment.fromMemberId(),
                    payment.toMemberId(),
                    Long.toString(payment.amountCents()),
                    payment.paymentMethod(),
                    payment.date().toString()));
        }

        writeCsvFile("groups.csv", GROUP_HEADER, groupRows);
        writeCsvFile("members.csv", MEMBER_HEADER, memberRows);
        writeCsvFile("expenses.csv", EXPENSE_HEADER, expenseRows);
        writeCsvFile("payments.csv", PAYMENT_HEADER, paymentRows);
    }

    @Override
    public AppData load() throws IOException {
        AppData appData = AppData.empty();
        Map<String, Group> groupsById = new LinkedHashMap<>();

        for (List<String> groupRow : readCsvFile("groups.csv")) {
            Group group = new Group(groupRow.get(0), groupRow.get(1));
            groupsById.put(group.getId(), group);
            appData.groups().add(group);
        }

        for (List<String> memberRow : readCsvFile("members.csv")) {
            String groupId = memberRow.get(1);
            Group group = groupsById.get(groupId);
            if (group != null) {
                group.addMember(new Member(memberRow.get(0), memberRow.get(2)));
            }
        }

        for (List<String> expenseRow : readCsvFile("expenses.csv")) {
            Expense expense = new Expense(
                    expenseRow.get(0),
                    expenseRow.get(1),
                    expenseRow.get(2),
                    Long.parseLong(expenseRow.get(3)),
                    expenseRow.get(4),
                    LocalDate.parse(expenseRow.get(5)),
                    expenseRow.get(6),
                    expenseRow.get(7),
                    decodeShares(expenseRow.get(8)));
            appData.expenses().add(expense);
        }

        for (List<String> paymentRow : readCsvFile("payments.csv")) {
            Payment payment = new Payment(
                    paymentRow.get(0),
                    paymentRow.get(1),
                    paymentRow.get(2),
                    paymentRow.get(3),
                    Long.parseLong(paymentRow.get(4)),
                    paymentRow.get(5),
                    LocalDate.parse(paymentRow.get(6)));
            appData.payments().add(payment);
        }

        return appData;
    }

    private void writeCsvFile(
            String fileName,
            List<String> header,
            List<List<String>> rows) throws IOException {
        StringBuilder csvContent = new StringBuilder(CsvUtil.formatRow(header));
        for (List<String> row : rows) {
            csvContent.append(CsvUtil.formatRow(row));
        }

        // Write to a temporary file first so an interrupted write leaves existing data intact.
        Path temporaryFile = dataDirectory.resolve(fileName + ".tmp");
        Files.writeString(temporaryFile, csvContent.toString(), StandardCharsets.UTF_8);
        Files.move(
                temporaryFile,
                dataDirectory.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);
    }

    /** Returns rows after the header; a missing file means there is no saved data yet. */
    private List<List<String>> readCsvFile(String fileName) throws IOException {
        Path csvFile = dataDirectory.resolve(fileName);
        if (!Files.exists(csvFile)) {
            return List.of();
        }

        List<List<String>> rows = CsvUtil.parse(
                Files.readString(csvFile, StandardCharsets.UTF_8));
        if (rows.isEmpty()) {
            return rows;
        }

        return rows.subList(1, rows.size());
    }

    private static String encodeShares(Map<String, Long> sharesByMemberId) {
        StringBuilder encodedShares = new StringBuilder();

        sharesByMemberId.forEach((memberId, shareCents) -> {
            if (encodedShares.length() > 0) {
                encodedShares.append(';');
            }
            encodedShares.append(memberId).append(':').append(shareCents);
        });

        return encodedShares.toString();
    }

    private static Map<String, Long> decodeShares(String encodedShares) {
        Map<String, Long> sharesByMemberId = new LinkedHashMap<>();
        if (encodedShares.isEmpty()) {
            return sharesByMemberId;
        }

        for (String encodedShare : encodedShares.split(";")) {
            int separatorIndex = encodedShare.lastIndexOf(':');
            String memberId = encodedShare.substring(0, separatorIndex);
            String shareCentsText = encodedShare.substring(separatorIndex + 1);
            sharesByMemberId.put(memberId, Long.parseLong(shareCentsText));
        }

        return sharesByMemberId;
    }
}
