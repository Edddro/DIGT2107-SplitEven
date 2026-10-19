package ca.spliteven.digt2107spliteven.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The outcome of checking one Add Expense submission.
 *
 * <p>Every failed check is collected rather than stopping at the first, so the
 * user can see everything that needs fixing in one go.</p>
 */
public class ValidationResult {

    private final List<ValidationError> errors;

    public ValidationResult(List<ValidationError> errors) {
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    /** True when nothing failed and the expense can be saved. */
    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    /** The message for a field, or null when that field passed its checks. */
    public String messageFor(ExpenseField field) {
        for (ValidationError error : errors) {
            if (error.getField() == field) {
                return error.getMessage();
            }
        }
        return null;
    }
}
