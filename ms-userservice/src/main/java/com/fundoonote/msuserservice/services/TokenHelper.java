package com.fundoonote.msuserservice.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenHelper 
{
	@Value("${app.name}")
	private String APP_NAME;

	@Value("${jwt.secret}")
	public String secret;

	@Value("${jwt.expires_in}")
	private int EXPIRES_IN;

	@Value("${jwt.header}")
	private String AUTH_HEADER;
	
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	private final Logger logger = LoggerFactory.getLogger(TokenHelper.class);

	private Map<String, Object> map;
	/**
	 * Generates a JWT token containing username as subject, and userId and role as
	 * additional claims. These properties are taken from the specified User object.
	 * Tokens validity is infinite.
	 *
	 * @param user
	 *            the user for which the token will be generating
	 * @return the JWT token
	 */
	public String generateToken(User user)
	{
		logger.info("inside generateToken()");
		map = new HashMap<>();
		map.put("id", user.getUserId());
		map.put("email", user.getEmail());
		map.put("role", user.getRole());
		String token = Jwts.builder().setIssuer(APP_NAME).setSubject("authToken").setClaims(map).setIssuedAt(new Date())
				.setExpiration(generateExpirationDate()).signWith(SIGNATURE_ALGORITHM, secret).compact();

		return token;
	}
	private Date generateExpirationDate()
	{
	   return new Date(new Date().getTime() + EXPIRES_IN * 1000);
	}

	public Map<String, Object> validateToken(String authToken) throws UserException {
		logger.info("inside validateToken()");

		if (!authToken.startsWith("Bearer ")) {
			authToken = authToken.replace("Bearer ", "");
		}

		try {
			final Claims claims = this.getAllClaimsFromToken(authToken);
			map = getMapFromIoJsonwebtokenClaims(claims);

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new UserException(105, e);
		}
		return map;
	}

	private Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims) {
		map = new HashMap<String, Object>();
		for (Entry<String, Object> entry : claims.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	private Claims getAllClaimsFromToken(String authToken) throws UserException {
		logger.info("inside getAllClaimsFromToken()");
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
		} catch (Exception e) {
			throw new UserException(105);
		}
		return claims;
	}

}
