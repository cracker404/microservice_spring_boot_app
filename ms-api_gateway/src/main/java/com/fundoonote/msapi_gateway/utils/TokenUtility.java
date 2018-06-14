package com.fundoonote.msapi_gateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@Component
public class TokenUtility 
{
	@Value("${security.jwt.client-secret}")
	private String key;

	public String verify(String token) 
	{
		JwtParser parser = Jwts.parser();
		Claims claims = parser.setSigningKey(key).parseClaimsJws(token).getBody();
		return claims.getIssuer();
	}
}
