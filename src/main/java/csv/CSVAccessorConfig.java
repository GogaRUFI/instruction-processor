package csv;

/**
 * CSVAccessorConfig - allows to configure data accessor (data source location ...)
 */

final public class CSVAccessorConfig {

    String csvFilePath;

    public CSVAccessorConfig(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }
}
