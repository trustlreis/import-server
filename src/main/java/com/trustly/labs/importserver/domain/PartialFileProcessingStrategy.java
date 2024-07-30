package com.trustly.labs.importserver.domain;

import com.trustly.labs.importserver.infrastructure.disk.FileManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartialFileProcessingStrategy extends AbstractProcessingStrategy {

    public PartialFileProcessingStrategy(final FileManager fileManager) {
        super(fileManager);
    }

    @Override
    public void process(final Stream<String[]> validRows, final Stream<String[]> invalidRows, final String fileName, final String jobName) {
        try {
            final List<String[]> validRowList = validRows.collect(Collectors.toList());
            final List<String[]> invalidRowList = invalidRows.collect(Collectors.toList());

            fileManager.saveValidRows(validRowList, fileName, jobName);
            if (!invalidRowList.isEmpty()) {
                fileManager.saveInvalidRows(invalidRowList, fileName, jobName);
            }
            logProcessingResult("File processed with some invalid rows.");
        } catch (final Exception e) {
            fileManager.saveInvalidFile(fileName, jobName, e);
        }
    }
}
