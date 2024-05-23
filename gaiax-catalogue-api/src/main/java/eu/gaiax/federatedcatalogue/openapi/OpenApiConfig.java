package eu.gaiax.federatedcatalogue.openapi;

import eu.gaiax.federatedcatalogue.appinfo.AppInfoConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final AppInfoConfiguration appInfoConfiguration;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info();
        info.setTitle(this.appInfoConfiguration.name());
        info.setDescription(this.appInfoConfiguration.description());
        info.setVersion(this.appInfoConfiguration.version());
        return new OpenAPI().info(info);
    }

    @Bean
    public GroupedOpenApi openApiDefinition() {
        return GroupedOpenApi.builder()
                .group("docs")
                .pathsToMatch("/**")
                .displayName("Docs")
                .build();
    }

}
