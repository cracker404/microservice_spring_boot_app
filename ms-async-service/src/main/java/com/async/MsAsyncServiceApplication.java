package com.async;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableHystrix
@EnableFeignClients
public class MsAsyncServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAsyncServiceApplication.class, args);
	}
}
