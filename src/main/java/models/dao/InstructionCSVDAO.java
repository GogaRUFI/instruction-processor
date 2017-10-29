package models.dao;

import csv.CSVAccessor;
import csv.CSVAccessorConfig;
import models.Instruction;
import models.InstructionType;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static models.dao.InstructionCSVFields.*;

/**
 * InstructionCSVDAO is a data access class for instructions:
 * - extends CSVAccessor and implements its interface for parsing instructions
 * - sets CSVAccessorConfig for CSVAccessor
 */

public class InstructionCSVDAO extends CSVAccessor<Instruction> {

    public InstructionCSVDAO(CSVAccessorConfig CSVAccessorConfig) {
        this.CSVAccessorConfig = CSVAccessorConfig;
    }

    public List<Instruction> getByReceivedDate(LocalDate start, LocalDate end) {
        return read().stream().filter(e ->
                (e.getReceivedDate().isAfter(start) || e.getReceivedDate().isEqual(start))
                        && (e.getReceivedDate().isBefore(end) || e.getReceivedDate().isEqual(end))
        ).collect(toList());
    }

    protected Optional<Instruction> parse(CSVRecord csvRecord) {
        try {
            return Optional.of(InstructionCSVParser.parse(csvRecord.toMap()));
        } catch (Throwable e) {
            System.out.println("Cant' parse row " + csvRecord);
            System.out.println(e.getMessage());
            return Optional.empty();
        }
    }
}


/**
 * InstructionCSVFields describes fields of a CSV file record for an instruction (is a part of InstructionCSVDAO)
 */

class InstructionCSVFields {
    static String Entity = "Entity";
    static String Type = "Buy/Sell";
    static String ExchangeRate = "AgreedFx";
    static String CurrencyCode = "Currency";
    static String ReceivedDate = "InstructionDate";
    static String SettlementDate = "SettlementDate";
    static String UnitQuantity = "Units";
    static String UnitPrice = "Price per unit";
}


/**
 * InstructionCSVParser is responsible for data matching from a CSV record to an instruction (is a part of InstructionCSVDAO)
 * - contains some logic responsible for data validation which can be omitted -
 *   assuming that CSV file stored in the system already has valid data.
 */

class InstructionCSVParser {

    private static String DatePattern = "dd MMM yyyy";
    private static DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern(DatePattern);

    static Instruction parse(Map<String, String> record) {
        return new Instruction(
                parseEntity(record.get(Entity)),
                parseInstructionType(record.get(Type)),
                parseExchangeRate(record.get(ExchangeRate)),
                parseCurrencyCode(record.get(CurrencyCode)),
                parseDate(record.get(ReceivedDate)),
                parseDate(record.get(SettlementDate)),
                parseQuantity(record.get(UnitQuantity)),
                parseUnitPrice(record.get(UnitPrice))
        );
    }

    private static String parseEntity(String value) {
        if (value.trim().isEmpty())
            throw new RuntimeException("Instruction's entity name can't be empty");
        else
            return value.trim();
    }

    private static InstructionType parseInstructionType(String value) {
        return InstructionType.getByName(value.trim()).orElseThrow(() ->
                new RuntimeException("Instruction type '$value' isn't supported")
        );
    }

    private static BigDecimal parseExchangeRate(String value) {
        BigDecimal result = parseBigDecimal(value);
        if (result.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Instruction's value '" + value + "' is expected to be a positive (non zero) number");
        }
        return result;
    }

    private static BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Instruction's value '" + value + "' is expected to be a decimal number");
        }
    }

    private static BigDecimal parseUnitPrice(String value) {
        BigDecimal result = parseBigDecimal(value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Instruction's value '" + value + "' is expected to be a positive number");
        }
        return result;
    }

    private static Currency parseCurrencyCode(String value) {
        try {
            return Currency.getInstance(value.trim());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Instruction's value '" + value + "' isn't a supported currency code");
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, DateFormat);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Instruction's value '" + value + "' isn't matching with required date format " + DatePattern);
        }
    }

    private static int parseQuantity(String value) {
        int result = parseInt(value);
        if (result < 0) {
            throw new RuntimeException("Instruction's value '$value' is expected to be a positive number");
        }
        return result;
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Instruction's value '" + value + "' is expected to be a number");
        }
    }
}


