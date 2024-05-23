package eu.gaiax.federatedcatalogue.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uniresolver.UniResolver;
import uniresolver.client.ClientUniResolver;

import java.net.URI;

@Configuration
public class UniResolverConfiguration {

    @Value("${gaiax.universal-did-resolver-url:https://dev.uniresolver.io/1.0/}")
    private String universalDidResolverUrl;

    @Bean
    UniResolver uniResolver() {
        return ClientUniResolver.create(URI.create(universalDidResolverUrl));
    }
}
