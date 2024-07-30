package com.trustly.labs.importserver.domain;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Service;
import com.trustly.labs.importserver.infrastructure.config.CsvConfig;
import com.trustly.labs.importserver.infrastructure.disk.FileManager;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class CsvParserService {

    private final Map<String, CsvConfig.LayoutConfig> layoutConfigs;
    private final Map<Boolean, ProcessingStrategy> processingStrategies;

    public CsvParserService(final Map<String, CsvConfig.LayoutConfig> layoutConfigs, final CsvConfig csvConfig, final FileManager fileManager) {
        this.layoutConfigs = layoutConfigs;
        this.processingStrategies = Map.of(
            true, new WholeFileProcessingStrategy(fileManager),
            false, new PartialFileProcessingStrategy(fileManager)
        );
    }

    public void parseCsv(final String layout, final InputStreamReader inputStreamReader, final String fileName, final String jobName) {
        final CsvConfig.LayoutConfig layoutConfig = Optional.ofNullable(layoutConfigs.get(layout))
                .orElseThrow(() -> new RuntimeException("Invalid layout: " + layout));

        try (CSVReader reader = new CSVReaderBuilder(inputStreamReader)
                .withSkipLines(layoutConfig.isHasHeader() ? 1 : 0)
                .build()) {

            final List<String[]> rows = reader.readAll();
            final List<CsvConfig.CsvColumnConfig> columns = layoutConfig.getColumns();

            final Stream<String[]> rowStream = rows.stream();
            final Stream<String[]> validRows = rowStream.filter(row -> isValidRow(row, columns));
            final Stream<String[]> invalidRows = rows.stream().filter(row -> !isValidRow(row, columns));

            processingStrategies.get(layoutConfig.isProcessWholeFile()).process(validRows, invalidRows, fileName, jobName);
        } catch (final Exception e) {
            processingStrategies.get(layoutConfig.isProcessWholeFile()).process(Stream.empty(), Stream.empty(), fileName, jobName);
        }
    }

    private boolean isValidRow(final String[] row, final List<CsvConfig.CsvColumnConfig> columns) {
        return IntStream.range(0, columns.size())
                .allMatch(i -> {
                    final CsvConfig.CsvColumnConfig column = columns.get(i);
                    return Optional.ofNullable(row[i])
                            .filter(column::validate)
                            .isPresent();
                });
    }
}
