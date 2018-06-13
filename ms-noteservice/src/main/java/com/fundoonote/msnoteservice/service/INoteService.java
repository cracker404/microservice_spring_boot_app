package com.fundoonote.msnoteservice.service;

import com.fundoonote.msnoteservice.model.Note;

public interface INoteService {

	public Note createNote(Note note);
	
	Iterable<Note> findAll();
	
}
