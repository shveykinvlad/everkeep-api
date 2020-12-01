package com.everkeep.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestConfig {

    @Bean
    public JdbcDatabaseContainer<?> jdbcDatabaseContainer() {
        var databaseContainer = new PostgreSQLContainer<>();
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
