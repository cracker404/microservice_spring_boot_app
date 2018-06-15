package com.fundoonote.msnoteservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.NotePreferences;

@Repository
public interface INotePrefDao extends JpaRepository<NotePreferences, Integer> {

	NotePreferences findByUserId(String loggedInUser);
}
