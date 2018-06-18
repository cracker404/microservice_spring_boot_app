package com.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.async.repository.ClientService;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableHystrix
@EnableFeignClients
@RestController
public class MsAsyncServiceApplication {
	
	@Autowired
	ClientService clientService;

	public static void main(String[] args) {
		SpringApplication.run(MsAsyncServiceApplication.class, args);
	}
	@GetMapping("/")
	public String send() {
		return clientService.send();
	}
}
