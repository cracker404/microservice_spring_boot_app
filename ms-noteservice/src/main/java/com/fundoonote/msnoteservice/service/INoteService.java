package com.fundoonote.msnoteservice.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msnoteservice.exception.NSException;
import com.fundoonote.msnoteservice.model.Collaboration;
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;

public interface INoteService {

	void saveNote(NoteDto noteDto, String userId) throws NSException;

	void updateNote(Note note, String userId) throws NSException;

	void updatenotePref(NotePreferences notePref, String loggedInUserId) throws NSException;

	void deleteNote(long noteId, String loggedInUser) throws NSException;

	List<NoteDto> getNotes(String loggedInUser);

	void saveLabel(Label label, String loggedInUserId) throws NSException;

	void renameLabel(Label label, String loggedInUserId) throws NSException;

	List<Label> getLabels(String userId);

	void deleteLabel(int labelId, String loggedInUserId) throws NSException;

	void addLabelToNote(long noteId, int labelId, String loggedInUserId);

	void saveLabelFromNote(Label label, long noteId, String loggedInUserId);

	void saveImage(MultipartFile image, long noteId);

	void deleteImage(int userId, long noteId, String key);

	void collaborate(String sharingUserEmail, long noteId, String loggedInUserEmail);

	void removeCollaborator(String sharedUserId, long noteId, String loggedInUserId) throws NSException;
	
	void pinOrUnpin(long notePrefId, boolean isPinned, String loggedInUserId) throws NSException;

	void archiveOrUnarchive(long notePrefId, Status status, String loggedInUserId) throws NSException;

	void trashOrRestore(long notePrefId, Status status, String loggedInUserId) throws NSException;


}
