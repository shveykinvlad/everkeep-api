package com.everkeep;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractIntegrationTest extends AbstractTest {

    private static final String POSTGRESQL_IMAGE = "postgres:14.4";

    private static final String SPRING_DATASOURCE_URL_PROPERTY = "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME_PROPERTY = "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD_PROPERTY = "spring.datasource.password";

    @Container
    private static final JdbcDatabaseContainer<?> CONTAINER = new PostgreSQLContainer<>(POSTGRESQL_IMAGE);

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper mapper;

    @DynamicPropertySource
    private static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add(SPRING_DATASOURCE_URL_PROPERTY, CONTAINER::getJdbcUrl);
        registry.add(SPRING_DATASOURCE_USERNAME_PROPERTY, CONTAINER::getUsername);
        registry.add(SPRING_DATASOURCE_PASSWORD_PROPERTY, CONTAINER::getPassword);
    }
}
