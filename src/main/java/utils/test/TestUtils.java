package utils.test;

import java.time.LocalDate;
import java.util.Currency;

/**
 * TestUtils - contains functionality commonly used in tests
 */

public class TestUtils {
    public static LocalDate getDate(int y, int m, int d) {
        return LocalDate.of(y, m, d);
    }

    public static Currency getCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
