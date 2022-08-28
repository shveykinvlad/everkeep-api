package com.everkeep.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@TestConfiguration
public class DatabaseConfig {

    private static final String POSTGRES_IMAGE = "postgres:14.4";

    @Bean
    public JdbcDatabaseContainer<?> jdbcDatabaseContainer() {
        var databaseContainer = new PostgreSQLContainer<>(POSTGRES_IMAGE);
        databaseContainer.start();

        return databaseContainer;
    }

    @Bean
    public DataSource dataSource(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        return DataSourceBuilder.create()
                .url(jdbcDatabaseContainer.getJdbcUrl())
                .username(jdbcDatabaseContainer.getUsername())
                .password(jdbcDatabaseContainer.getPassword())
                .build();
    }
}
