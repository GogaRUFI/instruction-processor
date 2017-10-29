package models;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * InstructionType is an enum which represents supported types of our instructions:
 * - allows to iterate through its values
 * - can be extended with custom fields (f.e. name)
 */

public enum InstructionType {
    BUY("B"),
    SELL("S");

    private final String name;

    InstructionType(String name) {
        this.name = name;
    }

    public static Stream<InstructionType> streamValues() {
        return Arrays.stream(values());
    }

    public static Optional<InstructionType> getByName(String name) {
        if (name != null) {
            for (InstructionType instructionType : InstructionType.values()) {
                if (name.equals(instructionType.name)) {
                    return Optional.of(instructionType);
                }
            }
        }
        return Optional.empty();
    }

    public String getName() {
        return name;
    }
}