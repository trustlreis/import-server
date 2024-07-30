package com.trustly.labs.importserver.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "csv")
public class SourceConfigLoader {
    private List<S3Source> s3;
    private List<SftpSource> sftp;

    @Data
    public static class S3Source {
        private String bucketName;
        private String layout;
        private String prefix;
        private String cron;
        private boolean enabled;
    }

    @Data
    public static class SftpSource {
        private String host;
        private int port;
        private String user;
        private String password;
        private String path;
        private String layout;
        private String cron;
        private boolean enabled;
    }
}
