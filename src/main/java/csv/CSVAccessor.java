package csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CSVAccessor is an abstract class which allows to get access to a CSV file to read any data from there:
 * - uses apache library to read CSV records from a CSV file
 * - can be configured through CSVAccessorConfig class
 * - provides an interface for parsing CSV records into a generic type
 */

public abstract class CSVAccessor<T> {

    protected CSVAccessorConfig CSVAccessorConfig;

    protected abstract Optional<T> parse(CSVRecord csvRecord);

    protected List<T> read() {
        try {
            File file = new File(CSVAccessorConfig.csvFilePath);
            CSVParser csvParser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT.withHeader());
            return csvParser.getRecords().stream()
                    .map(this::parse)
                    .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Can't read CSV file " + CSVAccessorConfig.csvFilePath);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
