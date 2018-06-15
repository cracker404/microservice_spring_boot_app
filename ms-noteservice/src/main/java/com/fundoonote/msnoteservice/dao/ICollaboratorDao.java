package com.fundoonote.msnoteservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Collaboration;


@Repository
public interface ICollaboratorDao extends JpaRepository<Collaboration, Integer> {

	/*@Query()
	void removeCollaborator();*/
}
