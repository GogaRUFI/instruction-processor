package services.instruction;

import models.Instruction;
import models.InstructionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ValueCalculatorTest {

    private ValueCalculator valueCalculator = new ValueCalculator();

    private static Instruction getInstruction(double unitPrice) {
        return getInstruction(unitPrice, 1, 1d);
    }

    private static Instruction getInstruction(double unitPrice, int unitQuantity) {
        return getInstruction(unitPrice, unitQuantity, 1d);
    }

    private static Instruction getInstruction(double unitPrice, double exchangeRate) {
        return getInstruction(unitPrice, 1, exchangeRate);
    }

    private static Instruction getInstruction(double unitPrice, int unitQuantity, double exchangeRate) {
        return new Instruction(
                "",
                InstructionType.BUY,
                BigDecimal.valueOf(exchangeRate),
                Currency.getInstance("USD"),
                LocalDate.now(),
                LocalDate.now(),
                unitQuantity,
                BigDecimal.valueOf(unitPrice)
        );
    }

    @Test
    public void calculateTotalAmount() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(10.2d));
        input.add(getInstruction(1.2d));
        input.add(getInstruction(100.2d));
        input.add(getInstruction(90.2d));
        input.add(getInstruction(0.0d));
        input.add(getInstruction(10.2d));
        input.add(getInstruction(0d));

        BigDecimal expected = BigDecimal.valueOf(212.000d);

        BigDecimal result = valueCalculator.calculate(input);

        assertEquals(result.compareTo(expected), 0);
    }

    @Test
    public void calculateTotalAmountWithExchangeRate() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(10.2d));
        input.add(getInstruction(1.2d, 1d));
        input.add(getInstruction(100.2008d, 1.4d));
        input.add(getInstruction(90.2d));
        input.add(getInstruction(0.0d, 0.6d));
        input.add(getInstruction(10.2d));
        input.add(getInstruction(0d));

        BigDecimal expected = BigDecimal.valueOf(252.08112d);

        BigDecimal result = valueCalculator.calculate(input);

        assertEquals(result.compareTo(expected), 0);
    }

    @Test
    public void calculateTotalAmountWithQuantity() {
        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(10.2d));
        input.add(getInstruction(1.2d));
        input.add(getInstruction(100.2d, 2));
        input.add(getInstruction(90.2d));
        input.add(getInstruction(0.0d, 3));
        input.add(getInstruction(10.2d));
        input.add(getInstruction(0d));

        BigDecimal expected = BigDecimal.valueOf(312.2d);

        BigDecimal result = valueCalculator.calculate(input);

        assertEquals(result.compareTo(expected), 0);
    }
}
