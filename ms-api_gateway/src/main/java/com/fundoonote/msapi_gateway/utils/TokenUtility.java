package com.fundoonote.msapi_gateway.utils;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

@Component
public class TokenUtility {

	private String KEY = "test-key";

	public String verify(String token) {
		JwtParser parser = Jwts.parser();
		Claims claims = parser.setSigningKey(KEY).parseClaimsJws(token).getBody();
		return claims.getIssuer();
	}
}
