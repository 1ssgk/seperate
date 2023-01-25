package com.backend.seperate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
   @Bean
   public CorsFilter corsFilter() {
      System.out.println("CorsConfig START ::::::::::::::::");
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.addAllowedOrigin("http://localhost:3009");
      //config.addAllowedOrigin("http://localhost");
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");

      source.registerCorsConfiguration("/api/**", config);
      System.out.println("CorsConfig END ::::::::::::::::");

      return new CorsFilter(source);
   }
}
