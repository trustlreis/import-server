package com.trustly.labs.importserver.domain;

import java.util.stream.Stream;

public interface ProcessingStrategy {
    void process(final Stream<String[]> validRows, final Stream<String[]> invalidRows, final String fileName, final String jobName);
}
