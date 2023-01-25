package com.backend.seperate.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class MyDataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.hikari")
  public DataSource dataSource(){
    return DataSourceBuilder
              .create()
              .type(HikariDataSource.class)
              .build();
  }
}
