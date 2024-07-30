package com.trustly.labs.importserver.application;

import com.trustly.labs.importserver.infrastructure.config.SourceConfigLoader;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SftpPollingRoute extends RouteBuilder {

    private final SourceConfigLoader sourceConfigLoader;

    @Override
    public void configure() throws Exception {
        sourceConfigLoader.getSftp().stream()
            .filter(SourceConfigLoader.SftpSource::isEnabled)
            .forEach(sftpSource -> {
                fromF("sftp://%s:%d/%s?username=%s&password=%s&delay=60000",
                    sftpSource.getHost(),
                    sftpSource.getPort(),
                    sftpSource.getPath(),
                    sftpSource.getUser(),
                    sftpSource.getPassword())
                    .to("direct:processCsvFile");
            });
    }
}
