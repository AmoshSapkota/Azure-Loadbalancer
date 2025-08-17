package com.project.webapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;

@SpringBootApplication
public class WebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebappApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ProductRepo repo) {
		return args -> {
			repo.save(new Product(1, "Laptop", 1000, "Electronics"));
			repo.save(new Product(2, "Phone", 500, "Electronics"));
			repo.save(new Product(3, "Tablet", 300, "Electronics"));
		};
	}

}
