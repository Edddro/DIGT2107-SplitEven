package ca.spliteven.digt2107spliteven.storage;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import ca.spliteven.digt2107spliteven.model.PaymentModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Stores groups, members, expenses, and payments in separate local CSV files. */
public class CsvStorage implements Storage {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String GROUP_ID = "groupId";
    private static final String CREATION_TIME = "creationTime";
    private static final String AMOUNT_CENTS = "amountCents";
    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String DATE = "date";
    private static final String CATEGORY = "category";
    private static final String NOTE = "note";
    private static final String PAID_BY_ID = "paidById";
    private static final String PAID_TO_ID = "paidToId";
    private static final String SHARED_BY_IDS = "sharedByIds";

    private static final String GROUP_FILE = "groups.csv";
    private static final String MEMBER_FILE = "members.csv";
    private static final String EXPENSE_FILE = "expenses.csv";
    private static final String PAYMENT_FILE = "payments.csv";

    private static final List<String> GROUP_HEADER = List.of(ID, NAME);
    private static final List<String> MEMBER_HEADER = List.of(ID, GROUP_ID, NAME);
    private static final List<String> EXPENSE_HEADER = List.of(
            ID, CREATION_TIME, GROUP_ID, AMOUNT_CENTS, PAYMENT_METHOD,
            DATE, CATEGORY, NOTE, PAID_BY_ID, SHARED_BY_IDS);
    private static final List<String> PAYMENT_HEADER = List.of(
            GROUP_ID, PAID_BY_ID, PAID_TO_ID, AMOUNT_CENTS);

    private final Path dataDirectory;

    public CsvStorage(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void save(AppData appData) throws IOException {
        Files.createDirectories(dataDirectory);
        List<List<String>> groupRows = new ArrayList<>();
        List<List<String>> memberRows = new ArrayList<>();
        List<List<String>> expenseRows = new ArrayList<>();
        List<List<String>> paymentRows = new ArrayList<>();

        for (GroupModel group : appData.getGroups()) {
            groupRows.add(List.of(group.getId().toString(), group.getName()));
            for (MemberModel member : group.getMembers()) {
                memberRows.add(List.of(
                        member.getId().toString(),
                        group.getId().toString(),
                        member.getName()));
            }
            for (ExpenseModel expense : appData.getExpenses(group)) {
                expenseRows.add(List.of(
                        expense.getId().toString(),
                        Long.toString(expense.getCreationTime()),
                        group.getId().toString(),
                        Long.toString(expense.getAmountCents()),
                        expense.getPaymentMethod().name(),
                        expense.getDate().toString(),
                        expense.getCategory(),
                        expense.getNote(),
                        expense.getPaidBy().getId().toString(),
                        encodeMemberIds(expense.getSharedBy())));
            }
            for (PaymentModel payment : appData.getPayments(group)) {
                paymentRows.add(List.of(
                        group.getId().toString(),
                        payment.getPaidBy().getId().toString(),
                        payment.getPaidTo().getId().toString(),
                        Long.toString(payment.getAmountCents())));
            }
        }

        writeCsvFile(GROUP_FILE, GROUP_HEADER, groupRows);
        writeCsvFile(MEMBER_FILE, MEMBER_HEADER, memberRows);
        writeCsvFile(EXPENSE_FILE, EXPENSE_HEADER, expenseRows);
        writeCsvFile(PAYMENT_FILE, PAYMENT_HEADER, paymentRows);
    }

    @Override
    public AppData load() throws IOException {
        AppData appData = AppData.empty();
        Map<UUID, GroupModel> groupsById = new HashMap<>();
        Map<UUID, MemberModel> membersById = new HashMap<>();

        for (List<String> groupRow : readCsvFile(GROUP_FILE)) {
            UUID groupId = parseUuid(groupRow.get(0), "group id");
            GroupModel group = new GroupModel(groupId, groupRow.get(1));
            groupsById.put(groupId, group);
            appData.addGroup(group);
        }

        for (List<String> memberRow : readCsvFile(MEMBER_FILE)) {
            UUID groupId = parseUuid(memberRow.get(1), "member group id");
            GroupModel group = requireGroup(groupsById, groupId);
            MemberModel member = new MemberModel(
                    parseUuid(memberRow.get(0), "member id"), memberRow.get(2));
            group.addMember(member);
            membersById.put(member.getId(), member);
        }

        for (List<String> expenseRow : readCsvFile(EXPENSE_FILE)) {
            UUID groupId = parseUuid(expenseRow.get(2), "expense group id");
            GroupModel group = requireGroup(groupsById, groupId);
            MemberModel payer = requireMember(membersById, expenseRow.get(8));
            List<MemberModel> sharers = decodeMembers(expenseRow.get(9), membersById);
            ExpenseModel expense = new ExpenseModel(
                    parseExpenseId(expenseRow.get(0)),
                    Long.parseLong(expenseRow.get(1)),
                    Long.parseLong(expenseRow.get(3)),
                    PaymentMethod.valueOf(expenseRow.get(4)),
                    LocalDate.parse(expenseRow.get(5)),
                    expenseRow.get(6),
                    expenseRow.get(7),
                    payer,
                    sharers);
            appData.expensesFor(group).add(expense);
        }

        for (List<String> paymentRow : readCsvFile(PAYMENT_FILE)) {
            UUID groupId = parseUuid(paymentRow.get(0), "payment group id");
            GroupModel group = requireGroup(groupsById, groupId);
            MemberModel payer = requireMember(membersById, paymentRow.get(1));
            MemberModel receiver = requireMember(membersById, paymentRow.get(2));
            PaymentModel payment = new PaymentModel(
                    payer, receiver, Long.parseLong(paymentRow.get(3)));
            appData.paymentsFor(group).add(payment);
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

        Path temporaryFile = dataDirectory.resolve(fileName + ".tmp");
        Files.write(
                temporaryFile,
                csvContent.toString().getBytes(StandardCharsets.UTF_8));
        Files.move(
                temporaryFile,
                dataDirectory.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);
    }

    private List<List<String>> readCsvFile(String fileName) throws IOException {
        Path csvFile = dataDirectory.resolve(fileName);
        if (!Files.exists(csvFile)) {
            return List.of();
        }

        String csvContent = new String(
                Files.readAllBytes(csvFile),
                StandardCharsets.UTF_8);
        List<List<String>> rows = CsvUtil.parse(csvContent);
        return rows.isEmpty() ? rows : rows.subList(1, rows.size());
    }

    private static String encodeMemberIds(List<MemberModel> members) {
        StringBuilder encodedIds = new StringBuilder();
        for (MemberModel member : members) {
            if (encodedIds.length() > 0) {
                encodedIds.append(';');
            }
            encodedIds.append(member.getId());
        }
        return encodedIds.toString();
    }

    private static List<MemberModel> decodeMembers(
            String encodedIds,
            Map<UUID, MemberModel> membersById) throws IOException {
        List<MemberModel> members = new ArrayList<>();
        if (encodedIds.isEmpty()) {
            return members;
        }
        for (String id : encodedIds.split(";")) {
            members.add(requireMember(membersById, id));
        }
        return members;
    }

    private static GroupModel requireGroup(Map<UUID, GroupModel> groupsById, UUID groupId)
            throws IOException {
        GroupModel group = groupsById.get(groupId);
        if (group == null) {
            throw new IOException("Saved data refers to missing group " + groupId);
        }
        return group;
    }

    private static MemberModel requireMember(Map<UUID, MemberModel> membersById, String id)
            throws IOException {
        UUID memberId = parseUuid(id, "member id reference");
        MemberModel member = membersById.get(memberId);
        if (member == null) {
            throw new IOException("Saved data refers to missing member " + memberId);
        }
        return member;
    }

    private static UUID parseUuid(String value, String fieldName) throws IOException {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException error) {
            throw new IOException("Invalid " + fieldName + " in saved data: " + value, error);
        }
    }

    private static UUID parseExpenseId(String value) throws IOException {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException invalidUuid) {
            try {
                long legacyId = Long.parseLong(value);
                if (legacyId <= 0) {
                    throw new NumberFormatException("ID must be greater than zero");
                }
                // Older CSV files used numeric expense IDs; keep their mapping stable on reload.
                String legacyKey = "spliteven:expense:" + legacyId;
                return UUID.nameUUIDFromBytes(legacyKey.getBytes(StandardCharsets.UTF_8));
            } catch (NumberFormatException invalidId) {
                throw new IOException("Invalid expense id in saved data: " + value, invalidId);
            }
        }
    }
}
