package com.async.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.async.response.Response;

public class ClientFallback implements ClientService {

	private Throwable throwable;
	
	private final Logger logger = LoggerFactory.getLogger(ClientFallback.class);

	public ClientFallback(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public ResponseEntity<Response> save(Object object, String index, String Id) {
		logger.error(throwable.getMessage());
		return null;
	}

	@Override
	public ResponseEntity<Response> update(Object object, String index, String Id) {
		logger.error(throwable.getMessage());
		return null;
	}

	@Override
	public Boolean deleteById(String index, String Id) {
		logger.error(throwable.getMessage());
		return false;
	}

	@Override
	public String send() {
		logger.error(throwable.getMessage());
		return "";
	}

}
