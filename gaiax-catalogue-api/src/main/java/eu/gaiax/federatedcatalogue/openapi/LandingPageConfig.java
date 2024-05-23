package eu.gaiax.federatedcatalogue.openapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LandingPageConfig implements WebMvcConfigurer {
    private final SwaggerUiConfigProperties properties;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String redirectUri = this.properties.getPath();
        log.trace("LandingPageConfig(addViewControllers): Set landing page to path -> {}", redirectUri);
        registry.addRedirectViewController("/", redirectUri);
    }
}
