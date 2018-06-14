package com.fundoonote.msuserservice.controllers;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fundoonote.msuserservice.models.LoginDto;
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

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginDto dto, HttpServletRequest req) {
		try {
			String token = userService.login(dto);
			HttpHeaders headers = new HttpHeaders();
			headers.add("token", token);
			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/test")
	public String testApi() {
		return "Hello " + UUID.randomUUID().toString();
	}
}
