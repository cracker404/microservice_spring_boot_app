package com.fundoonote.msnoteservice.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fundoonote.msnoteservice.dao.INoteDao;
import com.fundoonote.msnoteservice.model.Note;

@Component
public class NoteServiceImpl implements INoteService {
	
	private INoteDao iNoteDao;
	
	@Autowired
	public NoteServiceImpl(INoteDao iNoteDao) {
		this.iNoteDao  = iNoteDao;
	}

	@Override
	public Note createNote(Note note) {
		note.setCreateDate(new Date());
		note.setLastUpdated(new Date());
		Note createdNote = iNoteDao.save(note);
		return createdNote;
	}

	@Override
	public Iterable<Note> findAll() {

		return iNoteDao.findAll();
	}
}
