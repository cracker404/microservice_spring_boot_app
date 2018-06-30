package com.fundoonote.msnoteservice.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Collaboration;
import com.fundoonote.msnoteservice.model.Note;

@Repository
public interface ICollaboratorDao extends JpaRepository<Collaboration, Integer> {
	
	int deleteByNoteAndSharedId(Note note, Integer sharedId);

	List<Collaboration> getByNote(Note noteId);

	Collaboration getByNoteAndSharedId(Note note, Integer sharedUserId);


}
