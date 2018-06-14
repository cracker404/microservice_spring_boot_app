package com.fundoonote.msnoteservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.User;

@Repository
public interface IUserDao extends JpaRepository<User, Integer> {

	User findByEmail(String email);
}
