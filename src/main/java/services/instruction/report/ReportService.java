package services.instruction.report;

import models.Instruction;
import models.InstructionType;
import models.dao.InstructionCSVDAO;
import services.instruction.SettlementDateCalculator;
import services.instruction.ValueCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static services.instruction.report.ReportTablePrinter.printAmounts;
import static utils.data.ListUtils.groupToStream;
import static utils.data.MapUtils.zipWithRankByValue;

/* ReportService is responsible for generating a report:
    - formats and organises the data by grouping
    - uses other services and utilities for calculations
    - uses an injected data access object to read instructions
    - doesn't contain any logic which determines how the data will be used next (see: ReportTablePrinter)
*/

public class ReportService {

    private SettlementDateCalculator settlementDateCalculator;
    private ValueCalculator valueCalculator;
    private InstructionCSVDAO instructionCsvDAO;

    public ReportService(SettlementDateCalculator settlementDateCalculator,
                         ValueCalculator valueCalculator,
                         InstructionCSVDAO instructionCsvDAO) {
        this.settlementDateCalculator = settlementDateCalculator;
        this.valueCalculator = valueCalculator;
        this.instructionCsvDAO = instructionCsvDAO;
    }

    public void generateByReceivedDates(LocalDate start, LocalDate end) {
        List<Instruction> instructions = instructionCsvDAO.getByReceivedDate(start, end);
        printAmounts(calcAmountByDateAndType(instructions));
        calcAmountRankByTypeAndEntity(instructions).forEach(ReportTablePrinter::printRanks);
    }

    Map<LocalDate, Map<InstructionType, BigDecimal>> calcAmountByDateAndType(List<Instruction> instructions) {
        return groupToStream(instructions, groupingBy(this::calcSettlementDate))
                .collect(toMap(Entry::getKey, entry -> calcAmountByType(entry.getValue())));
    }

    Map<InstructionType, Map<String, Integer>> calcAmountRankByTypeAndEntity(List<Instruction> instructions) {
        return groupToStream(instructions, groupingBy(Instruction::getInstructionType))
                .collect(toMap(Entry::getKey, entry -> calcAmountRankByEntity(entry.getValue())));
    }

    private LocalDate calcSettlementDate(Instruction instruction) {
        return settlementDateCalculator.calculate(instruction);
    }

    private Map<InstructionType, BigDecimal> calcAmountByType(List<Instruction> instructions) {
        return groupToStream(instructions, groupingBy(Instruction::getInstructionType))
                .collect(toMap(Entry::getKey, entry -> calcAmount(entry.getValue())));
    }

    private BigDecimal calcAmount(List<Instruction> instructions) {
        return valueCalculator.calculate(instructions);
    }

    private Map<String, Integer> calcAmountRankByEntity(List<Instruction> instructions) {
        return zipWithRankByValue(calcAmountByEntity(instructions));
    }

    private Map<String, BigDecimal> calcAmountByEntity(List<Instruction> instructions) {
        return groupToStream(instructions, groupingBy(Instruction::getEntity))
                .collect(toMap(Entry::getKey, entry -> calcAmount(entry.getValue())));
    }
}
