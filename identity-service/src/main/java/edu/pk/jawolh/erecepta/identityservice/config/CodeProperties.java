package edu.pk.jawolh.erecepta.identityservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "application.codes")
public class CodeProperties {
    private Integer length;
    private Duration expiration;

}
