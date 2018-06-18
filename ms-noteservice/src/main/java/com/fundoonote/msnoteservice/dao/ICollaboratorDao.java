package com.fundoonote.msnoteservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fundoonote.msnoteservice.model.Collaboration;
import com.fundoonote.msnoteservice.model.Note;

@Repository
public interface ICollaboratorDao extends JpaRepository<Collaboration, Long> {
	
	@Transactional
	@Modifying
	void deleteByNoteAndSharedId(Note note, String sharedId);


}
