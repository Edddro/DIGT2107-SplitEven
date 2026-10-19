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
    @DisplayName("TC-001: an amount is converted to whole cents")
    void convertsToCents() {
        assertEquals(1234L, Money.parseToCents("12.34"));
        assertEquals(1200L, Money.parseToCents("12"));
        assertEquals(1230L, Money.parseToCents("12.3"));
        assertEquals(5L, Money.parseToCents("0.05"));
    }

    @Test
    @DisplayName("TC-002: a dollar sign and surrounding spaces are tolerated")
    void tidiesUpWhatTheUserTyped() {
        assertEquals(123456L, Money.parseToCents(" $1234.56 "));
    }

    @Test
    @DisplayName("TC-002b: a comma is rejected rather than guessed at")
    void rejectsCommasRatherThanGuessing() {
        // "12,34" is twelve dollars thirty-four in much of the world and twelve
        // hundred here. Guessing wrong by a factor of a hundred is not acceptable.
        assertFalse(Money.isValidAmount("12,34"));
        assertFalse(Money.isValidAmount("1,234.56"));
    }

    @ParameterizedTest(name = "TC-003: \"{0}\" is rejected as an amount")
    @ValueSource(strings = {"", "   ", "abc", "-5", "12.345", "1.2.3", "12,34", "1e5", "."})
    void rejectsAmountsItCannotStoreExactly(String text) {
        assertFalse(Money.isValidAmount(text));
        assertThrows(IllegalArgumentException.class, () -> Money.parseToCents(text));
    }

    @Test
    @DisplayName("TC-004: a null amount is rejected rather than crashing")
    void rejectsNull() {
        assertFalse(Money.isValidAmount(null));
    }

    @Test
    @DisplayName("TC-005: cents are formatted back with two decimal places")
    void formatsCents() {
        assertEquals("12.34", Money.format(1234L));
        assertEquals("0.05", Money.format(5L));
        assertEquals("12.00", Money.format(1200L));
    }

    @Test
    @DisplayName("TC-006: a hundredth of a cent never appears, however the amount is written")
    void roundTripsExactly() {
        assertTrue(Money.isValidAmount("0.10"));
        assertEquals(30L, Money.parseToCents("0.10") + Money.parseToCents("0.20"));
    }
}
