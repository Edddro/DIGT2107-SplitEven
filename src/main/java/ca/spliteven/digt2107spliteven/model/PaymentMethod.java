package ca.spliteven.digt2107spliteven.model;

/**
 * How an expense was paid for.
 *
 * <p>
 * An expense must name one of these before it can be saved .
 * </p>
 */
public enum PaymentMethod {
    CASH("Cash"),
    DEBIT("Debit"),
    CREDIT("Credit"),
    E_TRANSFER("E-transfer"),
    OTHER("Other");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    /** The name shown on screen. */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
