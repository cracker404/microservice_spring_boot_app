package com.fundoonote.msapi_gateway.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().and().authorizeRequests()
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.antMatchers("/actuator/**", "/api-docs/**", "/services/user/oauth/token", "/services/user/**", "/", "/services/user/save").permitAll()
				.antMatchers("/services/user/image","/services/user/profile","/services/user/test" ).hasAnyAuthority("USER")
				.antMatchers("/services/notes/**").hasAnyAuthority("USER").anyRequest().authenticated();
	}
}