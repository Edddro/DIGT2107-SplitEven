package ca.spliteven.digt2107spliteven.service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Converts between the text a user types and the whole cents the model stores
 *
 * 
 */
public final class Money {

    /** Digits, then at most two decimal places. */
    private static final Pattern AMOUNT = Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    private Money() {
    }

    /**
     * Tidies up what the user typed: drops a dollar sign and surrounding spaces.
     *
     * 
     */
    private static String clean(String text) {
        if (text == null) {
            return "";
        }
        return text.trim().replace("$", "").replace(" ", "");
    }

    /** True when the text is an amount this app can store exactly. */
    public static boolean isValidAmount(String text) {
        return AMOUNT.matcher(clean(text)).matches();
    }

    /**
     * Converts an amount such as {@code "12.34"} into {@code 1234} cents.
     *
     * @throws IllegalArgumentException if the text is not a valid amount
     * @throws ArithmeticException      if the amount is too large to hold in cents
     */
    public static long parseToCents(String text) {
        String cleaned = clean(text);
        if (!AMOUNT.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Not a valid amount: " + text);
        }
        return new BigDecimal(cleaned).movePointRight(2).longValueExact();
    }

    /**
     * Formats whole cents for display, for example {@code 1234} to {@code "12.34"}.
     */
    public static String format(long cents) {
        return BigDecimal.valueOf(cents, 2).toPlainString();
    }
}
