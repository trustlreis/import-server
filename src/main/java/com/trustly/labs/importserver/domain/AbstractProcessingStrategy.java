package com.trustly.labs.importserver.domain;

import com.trustly.labs.importserver.infrastructure.disk.FileManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractProcessingStrategy implements ProcessingStrategy {

    protected final FileManager fileManager;

    public AbstractProcessingStrategy(final FileManager fileManager) {
        this.fileManager = fileManager;
    }

    protected void logProcessingResult(final String message) {
        LOGGER.info(message);
    }
}
