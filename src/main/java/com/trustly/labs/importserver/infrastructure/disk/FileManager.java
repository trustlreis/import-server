package com.trustly.labs.importserver.infrastructure.disk;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class FileManager {

    private final String outputDirectory;

    public FileManager(@Value("${csv.outputDirectory}") final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void saveValidRows(final List<String[]> rows, final String fileName, final String jobName) {
        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        final String outputFileName = Path.of(outputDirectory, "valid_%s_%s_%s.csv".formatted(fileName, jobName, timestamp)).toString();
        saveFile(rows, outputFileName, null);
    }

    public void saveInvalidRows(final List<String[]> rows, final String fileName, final String jobName) {
        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        final String outputFileName = Path.of(outputDirectory, "invalid_%s_%s_%s.csv".formatted(fileName, jobName, timestamp)).toString();
        saveFile(rows, outputFileName, null);
    }

    public void saveInvalidFile(final String fileName, final String jobName, final Exception e) {
        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        final String outputFileName = Path.of(outputDirectory, "error_%s_%s_%s.csv".formatted(fileName, jobName, timestamp)).toString();
        saveFile(null, outputFileName, e);
    }

    private void saveFile(final List<String[]> rows, final String outputFileName, final Exception e) {
        try (FileWriter writer = new FileWriter(outputFileName)) {
            if (rows != null) {
                try (CSVWriter csvWriter = new CSVWriter(writer)) {
                    csvWriter.writeAll(rows);
                }
            } else if (e != null) {
                writer.write("Exception: " + e.getMessage());
            }
        } catch (final Exception ex) {
            LOGGER.error("Failed to write file: {}", ex.getMessage(), ex);
        }
    }
}
