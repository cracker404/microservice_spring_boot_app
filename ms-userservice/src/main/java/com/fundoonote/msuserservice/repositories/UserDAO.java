package com.fundoonote.msuserservice.repositories;

import org.springframework.data.repository.CrudRepository;

import com.fundoonote.msuserservice.models.User;

public interface UserDAO extends CrudRepository<User, Long> {

	User findByEmail(String email);
}

