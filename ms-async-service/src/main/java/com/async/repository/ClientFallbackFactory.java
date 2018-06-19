package com.async.repository;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

@Component
public class ClientFallbackFactory implements FallbackFactory<ClientService> {

	@Override
	public ClientService create(Throwable throwable) {
		return new ClientFallback(throwable);
	}

}
