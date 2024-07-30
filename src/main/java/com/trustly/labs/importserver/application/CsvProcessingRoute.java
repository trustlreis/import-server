package com.trustly.labs.importserver.application;

import com.trustly.labs.importserver.infrastructure.config.SourceConfigLoader;
import com.trustly.labs.importserver.domain.CsvParserService;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CsvProcessingRoute extends RouteBuilder {

    private final CsvParserService csvParserService;
    private final SourceConfigLoader sourceConfigLoader;

    @Override
    public void configure() throws Exception {
        from("direct:processCsvFile")
            .process(exchange -> {
                final InputStreamReader reader = new InputStreamReader(exchange.getIn().getBody(InputStream.class));
                final String fileName = exchange.getIn().getHeader("CamelAwsS3Key", String.class);
                final String bucketName = exchange.getIn().getHeader("CamelAwsS3BucketName", String.class);
                final String layout = sourceConfigLoader.getS3().stream()
                    .filter(s3Source -> s3Source.getBucketName().equals(bucketName))
                    .findFirst()
                    .map(SourceConfigLoader.S3Source::getLayout)
                    .orElseThrow(() -> new RuntimeException("No layout found for bucket: " + bucketName));
                csvParserService.parseCsv(layout, reader, fileName, bucketName);
            });
    }
}
