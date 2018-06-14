package com.fundoonote.msnoteservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Label;

@Repository
public interface ILabeDao extends JpaRepository<Label, Integer> {

}
