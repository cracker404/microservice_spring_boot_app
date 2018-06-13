package com.fundoonote.msuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MsUserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUserserviceApplication.class, args);
	}
}
