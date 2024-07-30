package com.trustly.labs.importserver.application;

import com.trustly.labs.importserver.infrastructure.config.SourceConfigLoader;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3PollingRoute extends RouteBuilder {

    private final SourceConfigLoader sourceConfigLoader;

    @Override
    public void configure() throws Exception {
        sourceConfigLoader.getS3().stream()
            .filter(SourceConfigLoader.S3Source::isEnabled)
            .forEach(s3Source -> {
                fromF("aws2-s3://%s?prefix=%s&delay=60000", s3Source.getBucketName(), s3Source.getPrefix())
                    .to("direct:processCsvFile");
            });
    }
}
