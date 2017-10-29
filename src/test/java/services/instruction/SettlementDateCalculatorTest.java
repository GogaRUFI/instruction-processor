package services.instruction;

import models.Instruction;
import models.InstructionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static services.instruction.SettlementDateCalculator.ShiftedWeekendCurrencies;
import static utils.test.TestUtils.getCurrency;
import static utils.test.TestUtils.getDate;

public class SettlementDateCalculatorTest {

    private static SettlementDateCalculator settlementDateCalculator = new SettlementDateCalculator();

    private static Currency getShiftedCurrency(int index) {
        return ShiftedWeekendCurrencies.get(index);
    }

    private static Instruction getInstruction(LocalDate settlementDate, Currency currency) {
        return getInstruction(settlementDate, settlementDate, currency);
    }

    private static Instruction getInstruction(LocalDate settlementDate, LocalDate receivedDate, Currency currency) {
        return new Instruction(
                "",
                InstructionType.BUY,
                BigDecimal.ONE,
                currency,
                receivedDate,
                settlementDate,
                0,
                BigDecimal.ONE
        );
    }

    @Test
    public void calculateSettlementDateWithDefaultWeekend() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), getCurrency("EUR")));
        input.add(getInstruction(getDate(2016, 2, 13), getCurrency("RUB")));
        input.add(getInstruction(getDate(2016, 3, 20), getCurrency("NGN")));
        input.add(getInstruction(getDate(2017, 3, 10), getCurrency("USD")));
        input.add(getInstruction(getDate(2017, 3, 4), getCurrency("EUR")));
        input.add(getInstruction(getDate(2017, 4, 9), getCurrency("RUB")));

        List<LocalDate> expected = new ArrayList<>();

        expected.add(getDate(2016, 2, 5));
        expected.add(getDate(2016, 2, 15));
        expected.add(getDate(2016, 3, 21));
        expected.add(getDate(2017, 3, 10));
        expected.add(getDate(2017, 3, 6));
        expected.add(getDate(2017, 4, 10));


        List<LocalDate> result = input.stream().map(settlementDateCalculator::calculate).collect(toList());

        assertEquals(expected, result);
    }

    @Test
    public void calculateExpiredSettlementDateWithDefaultWeekend() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), getDate(2016, 2, 6), getCurrency("EUR")));
        input.add(getInstruction(getDate(2016, 2, 13), getDate(2016, 2, 14), getCurrency("RUB")));

        List<LocalDate> expected = new ArrayList<>();

        expected.add(getDate(2016, 2, 8));
        expected.add(getDate(2016, 2, 15));

        List<LocalDate> result = input.stream().map(settlementDateCalculator::calculate).collect(toList());

        assertEquals(expected, result);
    }

    @Test
    public void calculateExpiredSettlementDateWithShiftedWeekend() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), getDate(2016, 2, 6), getShiftedCurrency(0)));
        input.add(getInstruction(getDate(2016, 2, 13), getDate(2016, 2, 15), getShiftedCurrency(1)));

        List<LocalDate> expected = new ArrayList<>();

        expected.add(getDate(2016, 2, 7));
        expected.add(getDate(2016, 2, 15));

        List<LocalDate> result = input.stream().map(settlementDateCalculator::calculate).collect(toList());

        assertEquals(expected, result);
    }

    @Test
    public void calculateSettlementDateWithShiftedWeekend() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), getShiftedCurrency(0)));
        input.add(getInstruction(getDate(2016, 2, 13), getShiftedCurrency(1)));
        input.add(getInstruction(getDate(2016, 3, 22), getShiftedCurrency(0)));
        input.add(getInstruction(getDate(2017, 3, 10), getShiftedCurrency(1)));
        input.add(getInstruction(getDate(2017, 3, 4), getShiftedCurrency(0)));
        input.add(getInstruction(getDate(2017, 4, 10), getShiftedCurrency(1)));


        List<LocalDate> expected = new ArrayList<>();

        expected.add(getDate(2016, 2, 7));
        expected.add(getDate(2016, 2, 14));
        expected.add(getDate(2016, 3, 22));
        expected.add(getDate(2017, 3, 12));
        expected.add(getDate(2017, 3, 5));
        expected.add(getDate(2017, 4, 10));


        List<LocalDate> result = input.stream().map(settlementDateCalculator::calculate).collect(toList());

        assertEquals(expected, result);
    }
}
