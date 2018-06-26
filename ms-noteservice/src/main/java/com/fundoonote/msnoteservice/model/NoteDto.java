package com.fundoonote.msnoteservice.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

public class NoteDto 
{
	private Note note;
	
	private MultipartFile image;
	
	private Set<Integer> collaboratorIds = new HashSet<>();
	
	private NotePreferences notePreferences;
	
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	public Set<Integer> getCollaboratorId() {
		return collaboratorIds;
	}
	public void setCollaboratorId(Set<Integer> collaboratorId) {
		collaboratorIds = collaboratorId;
	}
	public NotePreferences getNotePreferences() {
		return notePreferences;
	}
	public void setNotePreferences(NotePreferences notePreferences) {
		this.notePreferences = notePreferences;
	}
}
