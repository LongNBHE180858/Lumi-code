package com.lumi.config;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class DatabaseTestcontainer {

    private static final PostgreSQLContainer DATABASE_CONTAINER = new PostgreSQLContainer(
        DockerImageName.parse("pgvector/pgvector:pg18-trixie").asCompatibleSubstituteFor("postgres")
    )
        .withDatabaseName("lumi")

        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(DatabaseTestcontainer.class)))
        .withReuse(true);

    @Bean
    @ServiceConnection
    PostgreSQLContainer databaseContainer() {
        return DATABASE_CONTAINER;
    }
}
