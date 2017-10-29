package services.instruction;

import models.Instruction;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


/**
 * SettlementDateCalculator is responsible for calculation of an instruction's settlement date:
 * - list of currencies which define shifted weekends can be extended later
 */

public class SettlementDateCalculator {
    static List<Currency> ShiftedWeekendCurrencies = Arrays.asList(
            Currency.getInstance("AED"),
            Currency.getInstance("SAR")
    );

    private static List<Integer> WeekDays = range(DayOfWeek.MONDAY, DayOfWeek.SUNDAY);
    private static int WeekFirstDay = WeekDays.get(0);
    private static int WeekLastDay = WeekDays.get(WeekDays.size() - 1);

    private static List<Integer> WeekendDays = range(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static List<Integer> ShiftedWeekendDays = range(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY);

    private static List<Integer> range(DayOfWeek from, DayOfWeek to) {
        return IntStream.rangeClosed(from.getValue(), to.getValue()).boxed().collect(toList());
    }


    public LocalDate calculate(Instruction instruction) {
        return calculate(instruction.getSettlementDate(), instruction.getReceivedDate(), instruction.getCurrency());
    }

    private LocalDate calculate(LocalDate settlementDate, LocalDate receivedDate, Currency currency) {
        if (settlementDate.isBefore(receivedDate))
            return calculate(receivedDate, currency);
        else
            return calculate(settlementDate, currency);
    }

    private LocalDate calculate(LocalDate settlementDate, Currency currency) {
        if (ShiftedWeekendCurrencies.contains(currency))
            return adjust(settlementDate, ShiftedWeekendDays);
        else
            return adjust(settlementDate, WeekendDays);
    }

    private LocalDate adjust(LocalDate settlementDate, List<Integer> weekends) {
        int weekDay = settlementDate.getDayOfWeek().getValue();
        if (weekends.contains(weekDay))
            return settlementDate.plusDays(calculateDaysTillNextWorkDay(weekDay, weekends, 0));
        else
            return settlementDate;
    }

    private int calculateDaysTillNextWorkDay(int weekDay, List<Integer> weekends, int offset) {
        if (weekends.contains(weekDay))
            return calculateDaysTillNextWorkDay(calculateNextWeekDay(weekDay), weekends, offset + 1);
        else
            return offset;
    }

    private int calculateNextWeekDay(int weekDay) {
        if (weekDay == WeekLastDay)
            return WeekFirstDay;
        else
            return weekDay + 1;
    }
}
