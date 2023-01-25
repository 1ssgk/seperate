package com.backend.seperate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.backend.seperate.interceptors.JwtInterceptor;


@Configuration
public class JwtConfig implements WebMvcConfigurer{
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new JwtInterceptor())
            .addPathPatterns("/*")
            .excludePathPatterns("/sign/*");//.excludePathPatterns(null)
  }
  
  
   
}
