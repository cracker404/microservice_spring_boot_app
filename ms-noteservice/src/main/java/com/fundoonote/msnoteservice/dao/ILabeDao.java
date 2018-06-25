package com.fundoonote.msnoteservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Label;

@Repository
public interface ILabeDao extends JpaRepository<Label, Integer> {

	@Query(value = "SELECT nf FROM Label nf WHERE nf.userId = :userId")
	List<Label> getAllLabelsByUserId(@Param("userId") Integer userId);

	@Query("SELECT l FROM Label l WHERE l.name = :name and l.userId=:userId")
	Label findByNameAndUserId(@Param("name")String name, @Param("userId") Integer loggedInUserId);


}