package com.fundoonote.msuserservice;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtility {

	@Value("${jwt.secret}")
	private String key;
	
	public String generate(long userId) 
	{
		Date issuedAt = new Date();

		Date expiresAt = new Date(issuedAt.getTime() + 1000 * 60 * 60);

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		JwtBuilder builder = Jwts.builder();

		builder.setSubject("accessToken");

		builder.setIssuedAt(issuedAt);

		builder.setExpiration(expiresAt);

		builder.setIssuer(String.valueOf(userId));

		builder.signWith(signatureAlgorithm, key);

		String compactJwt = builder.compact();

		System.out.println("Generated jwt: " + compactJwt);

		return compactJwt;
	}
}
