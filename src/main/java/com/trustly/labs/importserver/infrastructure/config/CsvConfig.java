package com.trustly.labs.importserver.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "csv")
public class CsvConfig {
    private String outputDirectory;
    private Map<String, LayoutConfig> layouts;

    @Data
    public static class LayoutConfig {
        private boolean hasHeader;
        private boolean processWholeFile;
        private List<CsvColumnConfig> columns;
    }

    @Data
    public static class CsvColumnConfig {
        private String name;
        private String type;
        private boolean mandatory;
        private boolean nullable;
        private String regex;
        private List<String> enumValues;

        public boolean validate(String value) {
            if (!nullable && (value == null || value.isEmpty())) {
                return false;
            }
            if (regex != null && !value.matches(regex)) {
                return false;
            }
            if (enumValues != null && !enumValues.contains(value)) {
                return false;
            }
            return true;
        }
    }
}
