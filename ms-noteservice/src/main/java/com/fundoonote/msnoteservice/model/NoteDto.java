package com.fundoonote.msnoteservice.model;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

public class NoteDto {
	
	Note note;
	MultipartFile image;
	Set<Integer> CollaboratorId;
	NotePreferences notePreferences;
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
		return CollaboratorId;
	}
	public void setCollaboratorId(Set<Integer> collaboratorId) {
		CollaboratorId = collaboratorId;
	}
	public NotePreferences getNotePreferences() {
		return notePreferences;
	}
	public void setNotePreferences(NotePreferences notePreferences) {
		this.notePreferences = notePreferences;
	}
	
}
