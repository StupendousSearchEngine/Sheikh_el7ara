package com.example.SheikhEl7ara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SheikhEl7araApplication {

	public static void main(String[] args) {
		SpringApplication.run(SheikhEl7araApplication.class, args);
	}

	@GetMapping
	public ResponseEntity<String> hello(){
		return new ResponseEntity<String>("Hello From Spring Boot", HttpStatus.OK);
	}
}
