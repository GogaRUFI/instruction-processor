package models.dao;

import models.Instruction;
import models.InstructionType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static models.dao.InstructionCSVFields.*;
import static org.junit.Assert.assertEquals;

public class InstructionCSVParserTest {

    private static Map<String, String> validInstructionRecord = getValidInstructionRecord();

    private static Map<String, String> getValidInstructionRecord() {
        Map<String, String> instructionRecord = new HashMap<>();
        instructionRecord.put(ReceivedDate, "01 Jan 2016");
        instructionRecord.put(UnitQuantity, "200");
        instructionRecord.put(UnitPrice, "100.2509");
        instructionRecord.put(Entity, "Singapore");
        instructionRecord.put(SettlementDate, "02 Oct 2016");
        instructionRecord.put(CurrencyCode, "SGD");
        instructionRecord.put(ExchangeRate, "0.50");
        instructionRecord.put(Type, "B");

        return instructionRecord;
    }

    private static Map<String, String> getInstructionRecord(String key, String value) {
        Map<String, String> instructionRecord = getValidInstructionRecord();
        instructionRecord.replace(key, value);
        return instructionRecord;
    }


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseValidInstruction() {
        Instruction expected = new Instruction(
                "Singapore",
                InstructionType.BUY,
                BigDecimal.valueOf(0.50d),
                Currency.getInstance("SGD"),
                LocalDate.of(2016, 1, 1),
                LocalDate.of(2016, 10, 2),
                200,
                BigDecimal.valueOf(100.2509d)
        );

        Instruction result = InstructionCSVParser.parse(validInstructionRecord);

        assertInstructions(result, expected);
    }

    @Test
    public void parseWithInvalidUnitPrice() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(UnitPrice, "-100.25"));
        inputs.add(getInstructionRecord(UnitPrice, "text"));
        inputs.add(getInstructionRecord(UnitPrice, ""));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    @Test
    public void parseWithInvalidUnitQuantity() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(UnitQuantity, "-1"));
        inputs.add(getInstructionRecord(UnitQuantity, "1.0"));
        inputs.add(getInstructionRecord(UnitQuantity, "0.0"));
        inputs.add(getInstructionRecord(UnitQuantity, ""));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    @Test
    public void parseWithInvalidExchangeRate() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(ExchangeRate, "-1"));
        inputs.add(getInstructionRecord(ExchangeRate, "0"));
        inputs.add(getInstructionRecord(ExchangeRate, "0.0"));
        inputs.add(getInstructionRecord(ExchangeRate, ""));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    @Test
    public void parseWithInvalidCurrencyCode() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(CurrencyCode, ""));
        inputs.add(getInstructionRecord(CurrencyCode, "text"));
        inputs.add(getInstructionRecord(CurrencyCode, "123"));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    @Test
    public void parseWithInvalidEntity() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(Entity, ""));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    @Test
    public void parseWithInvalidInstructionType() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(Type, "text"));
        inputs.add(getInstructionRecord(Type, ""));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    @Test
    public void parseWithInvalidDateFormat() {
        List<Map<String, String>> inputs = new ArrayList<>();
        inputs.add(getInstructionRecord(ReceivedDate, "text"));
        inputs.add(getInstructionRecord(ReceivedDate, ""));
        inputs.add(getInstructionRecord(SettlementDate, ""));
        inputs.add(getInstructionRecord(SettlementDate, "text"));

        inputs.forEach(input -> {
            thrown.expect(RuntimeException.class);
            InstructionCSVParser.parse(input);
        });
    }

    private void assertInstructions(Instruction result, Instruction expected) {
        assertEquals(result.getEntity(), expected.getEntity());
        assertEquals(result.getInstructionType(), expected.getInstructionType());
        assertEquals(result.getCurrency(), expected.getCurrency());
        assertEquals(result.getSettlementDate(), expected.getSettlementDate());
        assertEquals(result.getExchangeRate().compareTo(expected.getExchangeRate()), 0);
        assertEquals(result.getReceivedDate(), expected.getReceivedDate());
        assertEquals(result.getUnitPrice().compareTo(expected.getUnitPrice()), 0);
        assertEquals(result.getUnitQuantity(), expected.getUnitQuantity());
    }
}
