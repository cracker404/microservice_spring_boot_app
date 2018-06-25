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

	void deleteNote(long noteId, Integer loggedInUser) throws NSException;

	List<NoteDto> getNotes(Integer loggedInUser);

	void saveLabel(Label label, Integer loggedInUserId) throws NSException;

	void renameLabel(Label label, Integer loggedInUserId) throws NSException;

	List<Label> getLabels(Integer loggedInUserId);

	void deleteLabel(int labelId, Integer loggedInUserId) throws NSException;

	void addLabelToNote(long noteId, int labelId, Integer loggedInUserId) throws NSException;

	void removeLabelFromNote(Label label, long noteId, Integer loggedInUserId) throws NSException;

	void saveImage(MultipartFile image, long noteId, Integer loggedInUserId) throws NSException;

	void deleteImage(Integer userId, long noteId, String key) throws NSException;

	void collaborate(String sharingUserEmail, long noteId, Integer loggedInUserEmail) throws NSException;

	void removeCollaborator(String sharedUserId, long noteId, Integer loggedInUserId) throws NSException;
	
	void pinOrUnpin(long notePrefId, boolean isPinned, Integer loggedInUserId) throws NSException;

	void archiveOrUnarchive(long notePrefId, Status status, Integer loggedInUserId) throws NSException;

	void trashOrRestore(long notePrefId, Status status, Integer loggedInUserId) throws NSException;


}
