package com.uj.stxtory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StxtoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(StxtoryApplication.class, args);
	}

}
