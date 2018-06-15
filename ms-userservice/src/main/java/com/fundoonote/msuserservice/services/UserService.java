package com.fundoonote.msuserservice.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fundoonote.msuserservice.chache.RedisService;
import com.fundoonote.msuserservice.models.RegistrationDto;
import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void register(RegistrationDto dto) 
	{
		User user = new User();
		user.setName(dto.getName());
		user.setContact(dto.getContact());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		userRepository.save(user);
		Map<String, Object> map = new HashMap<>();
		map.put("role", (user.getRole()).toUpperCase());
		redisService.save("USER", "user"+user.getId(), map);
	}

}
