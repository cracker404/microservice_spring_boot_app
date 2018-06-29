package com.fundoonote.msnoteservice.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;

@Repository
public interface INotePrefDao extends JpaRepository<NotePreferences, Integer> {

	NotePreferences findByUserId(Integer loggedInUser);
	
	@Query(value = "SELECT nf FROM NotePreferences nf WHERE nf.userId = :userId")
	List<NotePreferences> getAllNotePreferenceByUserId(@Param("userId")Integer loggedInUser);

	@Query(value = "SELECT nf FROM NotePreferences nf WHERE nf.note = :note and nf.userId = :userId")
	NotePreferences getByNoteAndUserId(@Param("note")Note note, @Param("userId") Integer userId);

	@Modifying
	List<NotePreferences> getAllNotePreferenceByUserIdAndStatus(Integer loggedInUser, Status status);

	@Modifying
	void deleteByUserId(Integer loggedInUserId);

	NotePreferences deleteByNoteAndUserId(Note note, Integer loggedInUserId);

	Optional<NotePreferences> findByNote(Note note);
}
