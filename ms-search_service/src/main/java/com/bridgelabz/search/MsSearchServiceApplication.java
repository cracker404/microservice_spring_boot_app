package com.bridgelabz.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MsSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSearchServiceApplication.class, args);
	}
}
