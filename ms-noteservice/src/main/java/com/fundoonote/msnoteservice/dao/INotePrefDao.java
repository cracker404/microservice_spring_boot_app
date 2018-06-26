package com.fundoonote.msnoteservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NotePreferences;

@Repository
public interface INotePrefDao extends JpaRepository<NotePreferences, Long> {

	NotePreferences findByUserId(Integer userId);
	
	@Query(value = "SELECT nf FROM NotePreferences nf WHERE nf.userId = :userId")
	List<NotePreferences> getAllNotePreferenceByUserId(@Param("userId")Integer loggedInUser);

	@Transactional
	@Query(value = "SELECT nf FROM NotePreferences nf WHERE nf.note = :note")
	NotePreferences getByNote(@Param("note")Note note);
}
