package unit;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class ContainerContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    public static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>(DockerImageName.parse("neo4j:4.2.10"))
            .withAdminPassword("password");

    private final PostgreSQLContainer postgreSQL = new PostgreSQLContainer("postgres:15.3");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        neo4jContainer.start();
        this.postgreSQL.start();
        Map<String, String> properties = new HashMap<>();
        properties.put("spring.datasource.url", this.postgreSQL.getJdbcUrl());
        properties.put("spring.datasource.username", this.postgreSQL.getUsername());
        properties.put("spring.datasource.password", this.postgreSQL.getPassword());
        properties.put("gaiax.neo4j.host", neo4jContainer.getHost());
        properties.put("gaiax.neo4j.port", neo4jContainer.getMappedPort(7687).toString());
        properties.put("gaiax.neo4j.username", "neo4j");
        properties.put("gaiax.neo4j.password", neo4jContainer.getAdminPassword());
        TestPropertyValues testProperties = TestPropertyValues.empty();

        testProperties.and(properties).applyTo(applicationContext.getEnvironment());
    }

    @Configuration
    public static class TestConfig {
        @Bean
        public org.neo4j.ogm.config.Configuration neo4jConfiguration() {
            return new org.neo4j.ogm.config.Configuration.Builder()
                    .uri(neo4jContainer.getBoltUrl())
                    .credentials("neo4j", neo4jContainer.getAdminPassword())
                    .build();
        }
    }

}
