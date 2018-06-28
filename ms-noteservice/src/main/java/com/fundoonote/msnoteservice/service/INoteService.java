package com.fundoonote.msnoteservice.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msnoteservice.exception.NSException;
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;

public interface INoteService {

	void saveNote(NoteDto noteDto, Integer loggedInUserId) throws NSException;

	void updateNote(Note note, Integer loggedInUserId) throws NSException;

	void updatenotePref(NotePreferences notePref, Integer loggedInUserId) throws NSException;

	void deleteNote(int noteId, Integer loggedInUser) throws NSException;

	List<NoteDto> getNotes(Integer loggedInUser);

	void saveLabel(Label label, Integer loggedInUserId) throws NSException;

	void renameLabel(Label label, Integer loggedInUserId) throws NSException;

	List<Label> getLabels(Integer loggedInUserId);

	void deleteLabel(int labelId, Integer loggedInUserId) throws NSException;

	//void addLabelToNote(int noteId, int labelId, Integer loggedInUserId) throws NSException;

	//void removeLabelFromNote(Label label, int noteId, Integer loggedInUserId) throws NSException;
	
	void addOrRemoveLabelFromNote(int noteId, int labelId, Integer loggedInUserId) throws NSException;

	void saveImage(MultipartFile image, int noteId, Integer loggedInUserId) throws NSException;

	void deleteImage(Integer userId, int noteId, String key) throws NSException;

	void collaborate(Integer sharingUserEmail, int noteId, Integer loggedInUserEmail) throws NSException;

	void removeCollaborator(Integer sharedUserId, int noteId, Integer loggedInUserId) throws NSException;
	
	void pinOrUnpin(int notePrefId, boolean isPinned, Integer loggedInUserId) throws NSException;

	void archiveOrUnarchive(int notePrefId, Status status, Integer loggedInUserId) throws NSException;

	void trashOrRestore(int noteId, Status status, Integer loggedInUserId) throws NSException;
	
	List<NoteDto> getNoteByStatus(Status status, Integer loggedInUserId);
}
