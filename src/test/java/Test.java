import org.junit.jupiter.api.io.TempDir;
import splitseven.controller.DataController;
import splitseven.model.AppData;
import splitseven.model.Expense;
import splitseven.model.Group;
import splitseven.model.Member;
import splitseven.model.Payment;
import splitseven.storage.CsvStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Test {
    @TempDir
    Path temporaryDirectory;

    private AppData createSampleData() {
        AppData sampleData = AppData.empty();
        Group roommatesGroup = Group.create("Roommates, \"Apt 4\"");
        Member ana = Member.create("Ana");
        Member bo = Member.create("Bo");
        roommatesGroup.addMember(ana);
        roommatesGroup.addMember(bo);
        sampleData.groups().add(roommatesGroup);
        sampleData.groups().add(Group.create("Empty group"));

        Map<String, Long> groceryShares = new LinkedHashMap<>();
        groceryShares.put(ana.id(), 501L);
        groceryShares.put(bo.id(), 500L);
        Expense groceryExpense = new Expense(
                "e1",
                roommatesGroup.getId(),
                ana.id(),
                1001,
                "Cash",
                LocalDate.of(2026, 10, 1),
                "Groceries",
                "milk, eggs\nand \"bread\"",
                groceryShares);
        sampleData.expenses().add(groceryExpense);

        Expense otherExpense = new Expense(
                "e2",
                roommatesGroup.getId(),
                bo.id(),
                200,
                "Card",
                LocalDate.of(2026, 10, 2),
                "Other",
                "",
                new LinkedHashMap<>(Map.of(ana.id(), 200L)));
        sampleData.expenses().add(otherExpense);

        Payment repayment = new Payment(
                "p1",
                roommatesGroup.getId(),
                bo.id(),
                ana.id(),
                300,
                "Cash",
                LocalDate.of(2026, 10, 3));
        sampleData.payments().add(repayment);

        return sampleData;
    }

    @org.junit.jupiter.api.Test
    void savingAndLoadingReturnsEqualData() throws IOException {
        AppData originalData = createSampleData();
        CsvStorage storage = new CsvStorage(temporaryDirectory);
        storage.save(originalData);

        AppData loadedData = storage.load();

        assertEquals(originalData, loadedData);
    }

    @org.junit.jupiter.api.Test
    void loadingWithoutSavedFilesReturnsEmptyData() throws IOException {
        Path missingDataDirectory = temporaryDirectory.resolve("missing");
        AppData loadedData = new CsvStorage(missingDataDirectory).load();

        assertTrue(loadedData.groups().isEmpty());
        assertTrue(loadedData.expenses().isEmpty());
        assertTrue(loadedData.payments().isEmpty());
    }

    @org.junit.jupiter.api.Test
    void savingCreatesCsvFileForEachDataType() throws IOException {
        CsvStorage storage = new CsvStorage(temporaryDirectory);
        storage.save(createSampleData());

        String[] csvFileNames = {
                "groups.csv",
                "members.csv",
                "expenses.csv",
                "payments.csv"
        };
        for (String csvFileName : csvFileNames) {
            assertTrue(Files.exists(temporaryDirectory.resolve(csvFileName)), csvFileName);
        }
    }

    @org.junit.jupiter.api.Test
    void savingAgainReplacesPreviouslySavedData() throws IOException {
        CsvStorage storage = new CsvStorage(temporaryDirectory);
        storage.save(createSampleData());
        storage.save(AppData.empty());

        assertTrue(storage.load().groups().isEmpty());
    }

    @org.junit.jupiter.api.Test
    void controllerSavesChangesSoTheyAreAvailableAfterRestart() throws IOException {
        DataController firstController = new DataController(
                new CsvStorage(temporaryDirectory));
        Group tripGroup = firstController.addGroup("Trip");
        Member cy = firstController.addMember(tripGroup, "Cy");

        DataController reopenedController = new DataController(
                new CsvStorage(temporaryDirectory));

        assertEquals(1, reopenedController.getGroups().size());
        Group savedGroup = reopenedController.getGroups().get(0);
        assertEquals(tripGroup, savedGroup);
        assertEquals(cy, savedGroup.getMembers().get(0));
    }
}
