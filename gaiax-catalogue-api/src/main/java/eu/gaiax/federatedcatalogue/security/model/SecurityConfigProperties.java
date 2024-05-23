package eu.gaiax.federatedcatalogue.security.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("gaiax.security")
public record SecurityConfigProperties(List<String> corsOrigins) {
}
