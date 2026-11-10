package ca.spliteven.digt2107spliteven.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for amounts being stored as whole cents (NFR-003). */
class MoneyTest {

    @Test
    @DisplayName("TC-029: money values are converted to whole cents")
    void tc029_moneyValuesConvertToWholeCents() {
        assertEquals(1234L, Money.parseToCents("12.34"));
        assertEquals(1200L, Money.parseToCents("12"));
        assertEquals(1230L, Money.parseToCents("12.3"));
        assertEquals(5L, Money.parseToCents("0.05"));
    }

    @Test
    @DisplayName("TC-029: input is converted to whole cents")
    void tc029_moneyInputConvertsToWholeCents() {
        assertEquals(123456L, Money.parseToCents(" $1234.56 "));
    }

    @Test
    @DisplayName("Supplementary: ambiguous comma formats are rejected")
    void rejectsAmbiguousCommaFormats() {
        // "12,34" is twelve dollars thirty-four in much of the world and twelve
        // hundred here. Guessing wrong by a factor of a hundred is not acceptable.
        assertFalse(Money.isValidAmount("12,34"));
        assertFalse(Money.isValidAmount("1,234.56"));
    }

    @ParameterizedTest(name = "Supplementary: \"{0}\" is rejected as an amount")
    @ValueSource(strings = {"", "   ", "abc", "1.2.3", "12,34", "1 2", "1e5", "."})
    void rejectsAmountsThatAreNotSupported(String text) {
        assertFalse(Money.isValidAmount(text));
        assertThrows(IllegalArgumentException.class, () -> Money.parseToCents(text));
    }


    @Test
    @DisplayName("TC-010: an amount cannot contain fractions of a cent")
    void tc010_rejectsFractionsOfACent() {
        assertFalse(Money.isValidAmount("12.345"));
        assertThrows(IllegalArgumentException.class, () -> Money.parseToCents("12.345"));
    }

    @Test
    @DisplayName("Supplementary: a null amount is rejected")
    void rejectsNullAmount() {
        assertFalse(Money.isValidAmount(null));
    }

    @Test
    @DisplayName("TC-029: displayed money keeps two decimal places")
    void tc029_formatsWholeCentsWithTwoDecimalPlaces() {
        assertEquals("12.34", Money.format(1234L));
        assertEquals("0.05", Money.format(5L));
        assertEquals("12.00", Money.format(1200L));
    }

    @Test
    @DisplayName("TC-029: money arithmetic stays in whole cents")
    void tc029_moneyArithmeticStaysInWholeCents() {
        assertTrue(Money.isValidAmount("0.10"));
        assertEquals(30L, Money.parseToCents("0.10") + Money.parseToCents("0.20"));
    }
}
