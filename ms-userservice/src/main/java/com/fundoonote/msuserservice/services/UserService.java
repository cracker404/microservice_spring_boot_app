package com.fundoonote.msuserservice.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fundoonote.msuserservice.TokenUtility;
import com.fundoonote.msuserservice.chache.RedisService;
import com.fundoonote.msuserservice.models.LoginDto;
import com.fundoonote.msuserservice.models.RegistrationDto;
import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private TokenUtility tokenUtility;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private RedisService redisService;
	
	public void register(RegistrationDto dto) 
	{
		User user = new User();
		user.setName(dto.getName());
		user.setContact(dto.getContact());
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());

		userRepository.save(user);
		Map<String, Object> map = new HashMap<>();
		map.put("role", (user.getRole()).toUpperCase());
		redisService.save("USER", "user"+user.getId(), map);
	}

	public String login(LoginDto dto) {
		User user = userRepository.findByEmail(dto.getEmail());
		
		if(user == null || !user.getPassword().equals(dto.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}
		
		return tokenUtility.generate(user.getId());
	}
}
