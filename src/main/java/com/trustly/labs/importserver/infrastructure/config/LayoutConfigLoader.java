package com.trustly.labs.importserver.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Configuration
public class LayoutConfigLoader {
    private final String layoutConfigPath;

    public LayoutConfigLoader(@Value("classpath:layouts.yml") String layoutConfigPath) {
        this.layoutConfigPath = layoutConfigPath;
    }

    @Bean
    public Map<String, CsvConfig.LayoutConfig> loadLayoutConfigs() {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            CsvConfig csvConfig = mapper.readValue(new File(layoutConfigPath), CsvConfig.class);
            return csvConfig.getLayouts();
        } catch (IOException e) {
            LOGGER.error("Failed to load layout configurations", e);
            throw new RuntimeException("Failed to load layout configurations", e);
        }
    }
}
