package com.example.poc.query.dynamic.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Value("${spring.liquibase.change-log:classpath:db/changelog/db.changelog-master.xml}")
    private String changeLog;

    @Value("${spring.liquibase.default-schema:public}")
    private String defaultSchema;

    @Value("${spring.liquibase.enabled:true}")
    private boolean enabled;

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLog);
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setShouldRun(enabled);
        liquibase.setDropFirst(false);
        liquibase.setClearCheckSums(true); // Limpa checksums antes de executar
        return liquibase;
    }
}

