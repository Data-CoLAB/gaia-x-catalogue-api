package eu.gaiax.federatedcatalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = "eu.gaiax.federatedcatalogue")
@EntityScan(basePackages = "eu.gaiax.federatedcatalogue.entity.postgres")
@EnableNeo4jRepositories(basePackages = {"eu.gaiax.federatedcatalogue.repository.neo4j"})
@EnableJpaRepositories(basePackages = {"eu.gaiax.federatedcatalogue.repository.postgres"})
@EnableFeignClients
@EnableScheduling
public class GaiaXFederatedCatalogueApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaiaXFederatedCatalogueApplication.class, args);
    }

}

