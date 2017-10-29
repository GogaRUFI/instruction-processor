import csv.CSVAccessorConfig;
import models.dao.InstructionCSVDAO;
import services.instruction.SettlementDateCalculator;
import services.instruction.ValueCalculator;
import services.instruction.report.ReportService;

import java.time.LocalDate;

/**
 * InstructionProcessor is a main class:
 * - has a section which defines all dependencies used by the service.
 *   It has to be replaced by a DI framework (Google Guice ...) for more complex projects.
 * - most of the dependecies are singletons (their instances can be reused in the code),
 *   generally a simple static class would be used for such a small project.
 *   Assuming that this is a part of a bigger project. In that case we would need a better way to manage dependencies.
 * - settings for CSVAccessorConfig dependency can be stored in a config file of our project instead of being hardcoded.
 */

public class InstructionProcessor {

    private static ValueCalculator valueCalculator = new ValueCalculator();
    private static SettlementDateCalculator settlementDateCalculator = new SettlementDateCalculator();

    private static CSVAccessorConfig CSVAccessorConfig = new CSVAccessorConfig("src/main/resources/Instructions.csv");
    private static InstructionCSVDAO instructionCsvDAO = new InstructionCSVDAO(CSVAccessorConfig);

    private static ReportService reportService = new ReportService(
            settlementDateCalculator,
            valueCalculator,
            instructionCsvDAO);

    public static void main(String args[]) {
        reportService.generateByReceivedDates(LocalDate.MIN, LocalDate.MAX);
    }
}
