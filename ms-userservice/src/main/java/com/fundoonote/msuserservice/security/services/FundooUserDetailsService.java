package com.fundoonote.msuserservice.security.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.repositories.UserDAO;

@Component
public class FundooUserDetailsService implements UserDetailsService {

	@Autowired
	UserDAO userDAO;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDAO.findByEmail(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("Invalid credentials");
		}
		
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		List<GrantedAuthority> authorities = Arrays.asList(authority);
		 UserDetails userDetails = new org.springframework.security.core.userdetails.
	                User(String.valueOf(user.getUserId()), user.getPassword(), authorities);
		return userDetails;
	}
}
