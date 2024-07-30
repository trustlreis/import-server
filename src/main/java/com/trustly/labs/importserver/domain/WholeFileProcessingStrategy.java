package com.trustly.labs.importserver.domain;

import com.trustly.labs.importserver.infrastructure.disk.FileManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WholeFileProcessingStrategy extends AbstractProcessingStrategy {

    public WholeFileProcessingStrategy(final FileManager fileManager) {
        super(fileManager);
    }

    @Override
    public void process(final Stream<String[]> validRows, final Stream<String[]> invalidRows, final String fileName, final String jobName) {
        final List<String[]> invalidRowList = invalidRows.collect(Collectors.toList());

        if (!invalidRowList.isEmpty()) {
            fileManager.saveInvalidRows(invalidRowList, fileName, jobName);
            logProcessingResult("File contains invalid rows. Processing stopped.");
        } else {
            try {
                final List<String[]> validRowList = validRows.collect(Collectors.toList());
                fileManager.saveValidRows(validRowList, fileName, jobName);
                logProcessingResult("File processed successfully.");
            } catch (final Exception e) {
                fileManager.saveInvalidFile(fileName, jobName, e);
            }
        }
    }
}
