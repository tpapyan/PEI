package com.example.appForPEI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.appForPEI.properties.StorageProperties;
import com.example.appForPEI.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class AppForPeiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppForPeiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
