package ca.spliteven.digt2107spliteven.service;

import java.util.Objects;

/** One failed check: the field it belongs to, and the message to show beside it. */
public class ValidationError {

    private final ExpenseField field;
    private final String message;

    public ValidationError(ExpenseField field, String message) {
        this.field = Objects.requireNonNull(field);
        this.message = Objects.requireNonNull(message);
    }

    public ExpenseField getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return field + ": " + message;
    }
}
