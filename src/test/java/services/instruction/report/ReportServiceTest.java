package services.instruction.report;

import models.Instruction;
import models.InstructionType;
import models.dao.InstructionCSVDAO;
import org.junit.Test;
import services.instruction.SettlementDateCalculator;
import services.instruction.ValueCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static utils.test.TestUtils.getDate;

public class ReportServiceTest {

    private static ValueCalculator valueCalculator = new ValueCalculator();
    private static SettlementDateCalculator settlementDateCalculator = new SettlementDateCalculator();

    private static InstructionCSVDAO instructionCsvDAO = mock(InstructionCSVDAO.class);

    private static services.instruction.report.ReportService reportService = new services.instruction.report.ReportService(
            settlementDateCalculator,
            valueCalculator,
            instructionCsvDAO);

    private static Instruction getInstruction(String entity, LocalDate settlementDate, LocalDate receivedDate, InstructionType instructionType, double price, double exchangeRate, int unitQuantity) {
        return new Instruction(
                entity,
                instructionType,
                BigDecimal.valueOf(exchangeRate),
                Currency.getInstance("USD"),
                receivedDate,
                settlementDate,
                unitQuantity,
                BigDecimal.valueOf(price)
        );
    }

    private static Instruction getInstruction(LocalDate settlementDate, LocalDate receivedDate, InstructionType instructionType, double price) {
        return getInstruction("", settlementDate, receivedDate, instructionType, price, 1d, 1);
    }

    private static Instruction getInstruction(LocalDate settlementDate, InstructionType instructionType, double price) {
        return getInstruction("", settlementDate, LocalDate.MIN, instructionType, price, 1d, 1);
    }

    private static Instruction getInstruction(LocalDate settlementDate, InstructionType instructionType, double price, int unitQuantity) {
        return getInstruction("", settlementDate, LocalDate.MIN, instructionType, price, 1d, unitQuantity);
    }

    private static Instruction getInstruction(LocalDate settlementDate, InstructionType instructionType, double price, double exchangeRate) {
        return getInstruction("", settlementDate, LocalDate.MIN, instructionType, price, exchangeRate, 1);
    }

    private static Instruction getInstruction(String entity, InstructionType instructionType, double price, double exchangeRate) {
        return getInstruction(entity, LocalDate.now(), LocalDate.MIN, instructionType, price, exchangeRate, 1);
    }

    private static Instruction getInstruction(String entity, InstructionType instructionType, double price) {
        return getInstruction(entity, LocalDate.now(), LocalDate.MIN, instructionType, price, 1d, 1);
    }

    private static Instruction getInstruction(String entity, InstructionType instructionType, double price, int unitQuantity) {
        return getInstruction(entity, LocalDate.now(), LocalDate.MIN, instructionType, price, 1d, unitQuantity);
    }


    @Test
    public void calcAmountByDateAndType() {

        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.BUY, 2.453d));
        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.BUY, 2.453d, 1.2d));
        input.add(getInstruction(getDate(2016, 8, 13), InstructionType.BUY, 234.567d));
        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.SELL, 100.7d, 4));
        input.add(getInstruction(getDate(2016, 5, 13), InstructionType.SELL, 23.56d));
        input.add(getInstruction(getDate(2016, 5, 13), getDate(2016, 5, 18), InstructionType.BUY, 203.56d));

        Map result = reportService.calcAmountByDateAndType(input);

        Map<LocalDate, Map<InstructionType, BigDecimal>> expected = new LinkedHashMap<>();

        Map<InstructionType, BigDecimal> record_1 = new HashMap<>();
        record_1.put(InstructionType.BUY, BigDecimal.valueOf(234.567d));

        Map<InstructionType, BigDecimal> record_2 = new HashMap<>();
        record_2.put(InstructionType.BUY, BigDecimal.valueOf(203.56d));

        Map<InstructionType, BigDecimal> record_3 = new HashMap<>();
        record_3.put(InstructionType.SELL, BigDecimal.valueOf(23.56d));

        Map<InstructionType, BigDecimal> record_4 = new HashMap<>();
        record_4.put(InstructionType.BUY, BigDecimal.valueOf(5.3966d));
        record_4.put(InstructionType.SELL, BigDecimal.valueOf(402.8d));

        expected.put(getDate(2016, 8, 15), record_1);
        expected.put(getDate(2016, 5, 18), record_2);
        expected.put(getDate(2016, 5, 13), record_3);
        expected.put(getDate(2016, 2, 5), record_4);

        assertEquals(expected, result);
    }


    @Test
    public void calcAmountByDateAndTypeWithExchangeRate() {

        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.SELL, 2.453d));
        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.BUY, 2.453d));
        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.BUY, 2.453d, 1.2d));

        Map result = reportService.calcAmountByDateAndType(input);

        Map<LocalDate, Map<InstructionType, BigDecimal>> expected = new LinkedHashMap<>();

        Map<InstructionType, BigDecimal> record = new HashMap<>();
        record.put(InstructionType.BUY, BigDecimal.valueOf(5.3966d));
        record.put(InstructionType.SELL, BigDecimal.valueOf(2.453d));

        expected.put(getDate(2016, 2, 5), record);

        assertEquals(expected, result);
    }


    @Test
    public void calcAmountByDateAndTypeWithUnitQuantity() {

        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.SELL, 2.453d));
        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.BUY, 2.453d));
        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.SELL, 100.7d, 4));

        Map result = reportService.calcAmountByDateAndType(input);

        Map<LocalDate, Map<InstructionType, BigDecimal>> expected = new LinkedHashMap<>();

        Map<InstructionType, BigDecimal> record = new HashMap<>();
        record.put(InstructionType.BUY, BigDecimal.valueOf(2.453d));
        record.put(InstructionType.SELL, BigDecimal.valueOf(405.253d));

        expected.put(getDate(2016, 2, 5), record);

        assertEquals(expected, result);
    }


    @Test
    public void calcAmountByDateAndTypeWithExpiredSettlementDate() {

        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction(getDate(2016, 2, 5), InstructionType.SELL, 2.453d));
        input.add(getInstruction(getDate(2016, 5, 13), getDate(2016, 5, 18), InstructionType.BUY, 203.56d));
        input.add(getInstruction(getDate(2016, 5, 18), InstructionType.BUY, 20d));

        Map result = reportService.calcAmountByDateAndType(input);

        Map<LocalDate, Map<InstructionType, BigDecimal>> expected = new LinkedHashMap<>();

        Map<InstructionType, BigDecimal> record_1 = new HashMap<>();
        record_1.put(InstructionType.SELL, BigDecimal.valueOf(2.453d));

        Map<InstructionType, BigDecimal> record_2 = new HashMap<>();
        record_2.put(InstructionType.BUY, BigDecimal.valueOf(223.56d));

        expected.put(getDate(2016, 2, 5), record_1);
        expected.put(getDate(2016, 5, 18), record_2);


        assertEquals(expected, result);
    }

    @Test
    public void calcAmountRankByTypeAndEntity() {

        List<Instruction> input = new ArrayList<>();

        input.add(getInstruction("first", InstructionType.BUY, 2.453d));
        input.add(getInstruction("first", InstructionType.BUY, 2.453d, 1.2d));
        input.add(getInstruction("second", InstructionType.BUY, 234.567d));
        input.add(getInstruction("fourth", InstructionType.SELL, 23.56d, 1.0d));
        input.add(getInstruction("second", InstructionType.SELL, 100.7d, 4));
        input.add(getInstruction("third", InstructionType.SELL, 23.56d));
        input.add(getInstruction("second", InstructionType.BUY, 203.56d));

        Map result = reportService.calcAmountRankByTypeAndEntity(input);

        Map<InstructionType, Map<String, Integer>> expected = new HashMap<>();

        Map<String, Integer> type_1 = new LinkedHashMap<>();
        type_1.put("second", 1);
        type_1.put("first", 2);

        Map<String, Integer> type_2 = new LinkedHashMap<>();
        type_2.put("second", 1);
        type_2.put("third", 2);
        type_2.put("fourth", 2);

        expected.put(InstructionType.BUY, type_1);
        expected.put(InstructionType.SELL, type_2);

        assertEquals(expected, result);
    }
}
