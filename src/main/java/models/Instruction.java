package models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

/**
 * Instruction represents an instruction in the project:
 * - doesn't carry any logic
 * - is immutable in our case (can be changed if required)
 */

public class Instruction {
    private String entity;
    private InstructionType instructionType;
    private BigDecimal exchangeRate;
    private Currency currency;
    private LocalDate receivedDate;
    private LocalDate settlementDate;
    private int unitQuantity;
    private BigDecimal unitPrice;

    public Instruction(String entity,
                       InstructionType instructionType,
                       BigDecimal exchangeRate,
                       Currency currency,
                       LocalDate receivedDate,
                       LocalDate settlementDate,
                       int unitQuantity,
                       BigDecimal unitPrice) {
        this.entity = entity;
        this.instructionType = instructionType;
        this.exchangeRate = exchangeRate;
        this.currency = currency;
        this.receivedDate = receivedDate;
        this.settlementDate = settlementDate;
        this.unitQuantity = unitQuantity;
        this.unitPrice = unitPrice;
    }

    public String getEntity() {
        return entity;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public int getUnitQuantity() {
        return unitQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
