import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import ca.spliteven.digt2107spliteven.model.PaymentModel;
import ca.spliteven.digt2107spliteven.service.ExpenseInput;
import ca.spliteven.digt2107spliteven.service.ExpenseService;
import ca.spliteven.digt2107spliteven.service.GroupService;
import ca.spliteven.digt2107spliteven.service.ValidationResult;
import ca.spliteven.digt2107spliteven.storage.CsvStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Test {
    @TempDir
    Path temporaryDirectory;

    private GroupModel createStandardGroup(GroupService groupService) {
        GroupModel group = new GroupModel("Apartment 4B");
        group.addMember(new MemberModel("Ana"));
        group.addMember(new MemberModel("Ben"));
        group.addMember(new MemberModel("Cai"));
        groupService.addGroup(group);
        return group;
    }

    private ExpenseModel addStandardExpense(GroupService groupService, GroupModel group) {
        List<MemberModel> members = group.getMembers();
        ExpenseInput input = new ExpenseInput(
                "45.00",
                members.get(0),
                PaymentMethod.DEBIT,
                LocalDate.of(2026, 10, 20),
                "Groceries",
                "No Frills run",
                members);
        ExpenseService expenseService = new ExpenseService(groupService);
        ValidationResult result = expenseService.add(group, input);
        assertTrue(result.isValid());
        return expenseService.expensesFor(group).get(0);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("TC-019 Changes are saved straight away")
    void tc019_changesAreSavedStraightAway() throws IOException {
        CsvStorage storage = new CsvStorage(temporaryDirectory);
        GroupService groupService = new GroupService(storage);
        GroupModel group = createStandardGroup(groupService);
        addStandardExpense(groupService, group);

        assertTrue(Files.exists(temporaryDirectory.resolve("groups.csv")));
        assertTrue(Files.exists(temporaryDirectory.resolve("members.csv")));
        assertTrue(Files.exists(temporaryDirectory.resolve("expenses.csv")));
        assertTrue(Files.exists(temporaryDirectory.resolve("payments.csv")));
        GroupService reloadedGroupService = new GroupService(storage);
        GroupModel reloadedGroup = reloadedGroupService.getGroups().get(0);
        assertEquals(1, reloadedGroupService.getGroups().size());
        assertEquals(3, reloadedGroup.getMembers().size());
        assertEquals(1, new ExpenseService(reloadedGroupService).countFor(reloadedGroup));
        assertEquals(4500L, new ExpenseService(reloadedGroupService)
                .expensesFor(reloadedGroup).get(0).getAmountCents());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("TC-020 Data reloads exactly")
    void tc020_dataReloadsExactly() throws IOException {
        CsvStorage storage = new CsvStorage(temporaryDirectory);
        GroupService originalGroupService = new GroupService(storage);
        GroupModel originalGroup = createStandardGroup(originalGroupService);
        List<MemberModel> originalMembers = originalGroup.getMembers();
        ExpenseInput expenseInput = new ExpenseInput(
                "45.00",
                originalMembers.get(0),
                PaymentMethod.DEBIT,
                LocalDate.of(2026, 10, 20),
                "Groceries",
                "No Frills, \"weekly\" run",
                Arrays.asList(originalMembers.get(0), originalMembers.get(2)));
        assertTrue(new ExpenseService(originalGroupService).add(originalGroup, expenseInput).isValid());
        PaymentModel originalPayment = new PaymentModel(
                originalMembers.get(1), originalMembers.get(0), 1500);
        originalGroupService.addPayment(originalGroup, originalPayment);
        ExpenseModel originalExpense = new ExpenseService(originalGroupService)
                .expensesFor(originalGroup).get(0);

        GroupService reloadedGroupService = new GroupService(storage);
        GroupModel reloadedGroup = reloadedGroupService.getGroups().get(0);
        ExpenseModel reloadedExpense = new ExpenseService(reloadedGroupService)
                .expensesFor(reloadedGroup).get(0);
        PaymentModel reloadedPayment = reloadedGroupService
                .getPayments(reloadedGroup).get(0);

        assertEquals(originalGroup.getId(), reloadedGroup.getId());
        assertEquals(originalGroup.getName(), reloadedGroup.getName());
        assertEquals(originalMembers.size(), reloadedGroup.getMembers().size());
        for (int memberIndex = 0; memberIndex < originalMembers.size(); memberIndex++) {
            assertEquals(originalMembers.get(memberIndex).getId(),
                    reloadedGroup.getMembers().get(memberIndex).getId());
            assertEquals(originalMembers.get(memberIndex).getName(),
                    reloadedGroup.getMembers().get(memberIndex).getName());
        }
        assertEquals(originalExpense.getId(), reloadedExpense.getId());
        assertEquals(originalExpense.getCreationTime(), reloadedExpense.getCreationTime());
        assertEquals(originalExpense.getAmountCents(), reloadedExpense.getAmountCents());
        assertEquals(originalExpense.getPaymentMethod(), reloadedExpense.getPaymentMethod());
        assertEquals(originalExpense.getDate(), reloadedExpense.getDate());
        assertEquals(originalExpense.getCategory(), reloadedExpense.getCategory());
        assertEquals(originalExpense.getNote(), reloadedExpense.getNote());
        assertEquals(originalExpense.getPaidBy().getId(), reloadedExpense.getPaidBy().getId());
        assertEquals(2, reloadedExpense.getSharedBy().size());
        assertEquals(originalExpense.getSharedBy().get(0).getId(),
                reloadedExpense.getSharedBy().get(0).getId());
        assertEquals(originalExpense.getSharedBy().get(1).getId(),
                reloadedExpense.getSharedBy().get(1).getId());
        assertEquals(originalPayment.getPaidBy().getId(), reloadedPayment.getPaidBy().getId());
        assertEquals(originalPayment.getPaidTo().getId(), reloadedPayment.getPaidTo().getId());
        assertEquals(originalPayment.getAmountCents(), reloadedPayment.getAmountCents());
    }

    @org.junit.jupiter.api.Test
    @DisplayName("TC-021 First start with no data")
    void tc021_firstStartWithNoData() throws IOException {
        Path emptyDataDirectory = temporaryDirectory.resolve("empty");
        GroupService groupService = new GroupService(new CsvStorage(emptyDataDirectory));

        assertTrue(groupService.getGroups().isEmpty());
        assertFalse(Files.exists(emptyDataDirectory));
    }
}
