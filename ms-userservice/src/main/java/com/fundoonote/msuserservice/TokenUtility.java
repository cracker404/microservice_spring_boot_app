package com.fundoonote.msuserservice;

import java.util.Date;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenUtility {

	public static final String KEY = "test-key";

	public static String generate(long userId) {

		Date issuedAt = new Date();

		Date expiresAt = new Date(issuedAt.getTime() + 1000 * 60 * 60);

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		JwtBuilder builder = Jwts.builder();

		builder.setSubject("accessToken");

		builder.setIssuedAt(issuedAt);

		builder.setExpiration(expiresAt);

		builder.setIssuer(String.valueOf(userId));

		builder.signWith(signatureAlgorithm, KEY);

		String compactJwt = builder.compact();

		System.out.println("Generated jwt: " + compactJwt);

		return compactJwt;
	}
}
