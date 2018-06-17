package com.async.repository;

import com.async.model.Email;

public interface ClientService {

	void save(Object object);

	void update(Object object);

	void deleteById(int id);

	void send(Email email);

}
