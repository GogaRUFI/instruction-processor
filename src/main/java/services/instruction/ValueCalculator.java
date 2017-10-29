package services.instruction;

import models.Instruction;

import java.math.BigDecimal;
import java.util.List;

/**
 * ValueCalculator is responsible for calculation of instructions' total financial value:
 * - can be used for calculation of an individual instruction's value too
 */

public class ValueCalculator {

    public BigDecimal calculate(List<Instruction> instructions) {
        return instructions.stream().map(this::calculate).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculate(Instruction instruction) {
        return calculate(instruction.getUnitPrice(), instruction.getExchangeRate(), instruction.getUnitQuantity());
    }

    private BigDecimal calculate(BigDecimal amount, BigDecimal rate, int quantity) {
        return amount.multiply(rate).multiply(new BigDecimal(quantity)).stripTrailingZeros();
    }
}
