package utils;

import java.util.stream.IntStream;

/**
 * ConsoleUtils contains commonly used functionality related to printing data to a console
 */

public class ConsoleUtils {

    public static void printPaddedRight(String value, int padding) {
        System.out.print(String.format("%1$-" + padding + "s", value));
    }

    public static void printNewLine() {
        printNewLine(1);
    }

    public static void printNewLine(int times) {
        IntStream.range(0, times).forEach(e -> System.out.println());
    }
}
