package ca.spliteven.digt2107spliteven.ui;

import ca.spliteven.digt2107spliteven.model.ExpenseModel;
import ca.spliteven.digt2107spliteven.model.GroupModel;
import ca.spliteven.digt2107spliteven.model.MemberModel;
import ca.spliteven.digt2107spliteven.model.PaymentMethod;
import ca.spliteven.digt2107spliteven.service.ExpenseField;
import ca.spliteven.digt2107spliteven.service.ExpenseInput;
import ca.spliteven.digt2107spliteven.service.Money;
import ca.spliteven.digt2107spliteven.service.ValidationResult;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The Add Expense form
 *
 *
 * This class only collects input and displays what it is told to display.
 */
public class AddExpenseView {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy");

    private static final List<String> CATEGORIES = new ArrayList<>(java.util.Arrays.asList(
            "Food", "Groceries", "Rent", "Utilities", "Transport", "Entertainment", "Other"));

    private final GroupModel group;
    private final BorderPane root = new BorderPane();

    private final TextField amountField = new TextField();
    private final ComboBox<MemberModel> paidByBox = new ComboBox<>();
    private final ComboBox<PaymentMethod> paymentMethodBox = new ComboBox<>();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final TextField noteField = new TextField();
    private final List<CheckBox> sharedByBoxes = new ArrayList<>();

    private final Map<ExpenseField, Label> errorLabels = new EnumMap<>(ExpenseField.class);
    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");
    private final Label messageLabel = new Label();
    private final ListView<String> historyList = new ListView<>();

    public AddExpenseView(GroupModel group) {
        this.group = group;

        final Label title = new Label("Add Expense  —  " + group.getName());
        title.setFont(Font.font(18));
        title.setPadding(new Insets(12, 12, 0, 12));

        paidByBox.setItems(FXCollections.observableArrayList(group.getMembers()));
        paymentMethodBox.setItems(FXCollections.observableArrayList(PaymentMethod.values()));
        categoryBox.setItems(FXCollections.observableArrayList(CATEGORIES));
        amountField.setPromptText("12.34");
        noteField.setPromptText("optional");

        final VBox form = new VBox(2);
        form.setPadding(new Insets(12));
        form.getChildren().addAll(
                field("Amount", amountField, ExpenseField.AMOUNT),
                field("Paid by", paidByBox, ExpenseField.PAID_BY),
                field("Payment method", paymentMethodBox, ExpenseField.PAYMENT_METHOD),
                field("Date", datePicker, ExpenseField.DATE),
                field("Category", categoryBox, ExpenseField.CATEGORY),
                field("Note", noteField, null),
                field("Shared by", sharedByControl(), ExpenseField.SHARED_BY));

        final ScrollPane formScroll = new ScrollPane(form);
        formScroll.setFitToWidth(true);

        final Label historyTitle = new Label("Expense history");
        historyTitle.setFont(Font.font(14));
        final VBox history = new VBox(6, historyTitle, historyList);
        history.setPadding(new Insets(12));
        VBox.setVgrow(historyList, Priority.ALWAYS);
        historyList.setPlaceholder(new Label("No expenses yet."));

        final HBox columns = new HBox(formScroll, history);
        HBox.setHgrow(formScroll, Priority.ALWAYS);
        HBox.setHgrow(history, Priority.ALWAYS);
        formScroll.setPrefWidth(360);
        history.setPrefWidth(360);

        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);
        final ToolBar actions = new ToolBar(saveButton, cancelButton, messageLabel);

        root.setTop(title);
        root.setCenter(columns);
        root.setBottom(actions);
    }

    /** One labelled control, with the space for its error message underneath. */
    private VBox field(String labelText, Node control, ExpenseField field) {
        final Label label = new Label(labelText);
        final VBox box = new VBox(2, label, control);
        box.setPadding(new Insets(6, 0, 6, 0));
        if (control instanceof ComboBox) {
            ((ComboBox<?>) control).setMaxWidth(Double.MAX_VALUE);
        }
        if (field != null) {
            final Label error = new Label();
            error.setStyle("-fx-text-fill: #b3261e;");
            error.setWrapText(true);
            error.setVisible(false);
            error.setManaged(false);
            errorLabels.put(field, error);
            box.getChildren().add(error);
        }
        return box;
    }

    /** A tick box per member, because an expense can be shared by any of them. */
    private Node sharedByControl() {
        final VBox box = new VBox(2);
        for (MemberModel member : group.getMembers()) {
            final CheckBox checkBox = new CheckBox(member.getName());
            checkBox.setUserData(member);
            checkBox.setSelected(true);
            sharedByBoxes.add(checkBox);
            box.getChildren().add(checkBox);
        }
        return box;
    }

    /** What the user has entered right now, untouched and unchecked. */
    public ExpenseInput readValues() {
        final List<MemberModel> sharedBy = new ArrayList<>();
        for (CheckBox checkBox : sharedByBoxes) {
            if (checkBox.isSelected()) {
                sharedBy.add((MemberModel) checkBox.getUserData());
            }
        }
        return new ExpenseInput(
                amountField.getText(),
                paidByBox.getValue(),
                paymentMethodBox.getValue(),
                datePicker.getValue(),
                categoryBox.getValue(),
                noteField.getText(),
                sharedBy);
    }

    /** Puts each message beside the field that caused it. */
    public void showErrors(ValidationResult result) {
        for (Map.Entry<ExpenseField, Label> entry : errorLabels.entrySet()) {
            final String message = result.messageFor(entry.getKey());
            final Label label = entry.getValue();
            label.setText(message == null ? "" : message);
            label.setVisible(message != null);
            label.setManaged(message != null);
        }
        messageLabel.setStyle("-fx-text-fill: #b3261e;");
        messageLabel.setText(result.isValid() ? "" : "Nothing was saved. Fix the fields marked above.");
    }

    public void showConfirmation(String message) {
        messageLabel.setStyle("-fx-text-fill: #1b6b32;");
        messageLabel.setText(message);
    }

    public void showSaveError(String message) {
        messageLabel.setStyle("-fx-text-fill: #b3261e;");
        messageLabel.setText(message);
    }

    public void clearMessages() {
        for (Label label : errorLabels.values()) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        }
        messageLabel.setText("");
    }

    /** Empties the form so the next expense can be typed straight in. */
    public void clearForm() {
        amountField.clear();
        paidByBox.getSelectionModel().clearSelection();
        paymentMethodBox.getSelectionModel().clearSelection();
        categoryBox.getSelectionModel().clearSelection();
        noteField.clear();
        datePicker.setValue(LocalDate.now());
        for (CheckBox checkBox : sharedByBoxes) {
            checkBox.setSelected(true);
        }
        amountField.requestFocus();
    }

    /** Shows the group's saved expenses, newest first. */
    public void setHistory(List<ExpenseModel> expenses) {
        final List<String> rows = new ArrayList<>();
        for (int i = expenses.size() - 1; i >= 0; i--) {
            rows.add(describe(expenses.get(i)));
        }
        historyList.setItems(FXCollections.observableArrayList(rows));
    }

    private String describe(ExpenseModel expense) {
        final StringBuilder row = new StringBuilder();
        row.append(expense.getDate().format(DATE_FORMAT))
                .append("  ·  $").append(Money.format(expense.getAmountCents()))
                .append("  ·  ").append(expense.getCategory())
                .append("  ·  paid by ").append(expense.getPaidBy().getName())
                .append(" (").append(expense.getPaymentMethod().getLabel()).append(")")
                .append("  ·  shared by ").append(expense.getSharedBy().size());
        if (!expense.getNote().isEmpty()) {
            row.append("  ·  ").append(expense.getNote());
        }
        return row.toString();
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public BorderPane getRoot() {
        return root;
    }
}
