package com.fundoonote.msuserservice.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fundoonote.msuserservice.models.RegistrationDto;
import com.fundoonote.msuserservice.services.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegistrationDto dto) {
		try {
			userService.register(dto);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<String>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/test")
	public String testApi(@RequestParam String userid) {
		System.out.println("Inside Test Api");
		return "Hello " + userid + " " + UUID.randomUUID().toString();
	}
}
