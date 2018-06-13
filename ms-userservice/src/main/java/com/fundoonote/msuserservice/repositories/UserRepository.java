package com.fundoonote.msuserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fundoonote.msuserservice.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);
}
