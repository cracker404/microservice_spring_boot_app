package com.fundoonote.msnoteservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.NotePreferences;

@Repository
public interface INotePrefDao extends JpaRepository<NotePreferences, Long> {

	NotePreferences findByUserId(String loggedInUser);
	
	@Query(value = "SELECT nf FROM NotePreferences nf WHERE nf.userId = :userId")
	List<NotePreferences> getAllNotePreferenceByUserId(@Param("userId")String userId);
}
