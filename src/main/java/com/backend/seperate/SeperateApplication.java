package com.backend.seperate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SeperateApplication {

	public static void main(String[] args) {
		System.out.println("=====  SeperateApplication main =====");
		SpringApplication.run(SeperateApplication.class, args);
	}

}
//6fe126eb-ccaf-4a71-bb90-2ba90fe89728