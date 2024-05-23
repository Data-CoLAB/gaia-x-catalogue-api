package eu.gaiax.federatedcatalogue.appinfo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gaiax")
public record AppInfoConfiguration(String name,
                                   String description,
                                   String version) {
}
