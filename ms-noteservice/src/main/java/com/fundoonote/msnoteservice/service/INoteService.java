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

	void saveNote(NoteDto noteDto, String userId) throws NSException;

	void updateNote(Note note) throws NSException;

	void updatenotePref(NotePreferences notePref);

	void deleteNote(int noteId) throws NSException;

	List<NoteDto> getNotes(String loggedInUser);

	void saveLabel(Label label, String loggedInUserId);

	void renameLabel(Label label, String loggedInUserId);

	List<Label> getLabels();

	void deleteLabel(int labelId);

	void addRemoveLabel(int noteId, int labelId);

	void saveLabelFromNote(Label label, int noteId, String loggedInUserId);

	void saveImage(MultipartFile image, int noteId);

	void deleteImage(int userId, int noteId, String key);

	void collaborat(String sharingUserEmail, int noteId, String loggedInUserEmail);

	void removeCollaboratUser();

	void pinOrUnpin(int notePrefId, boolean isPinned, String loggedInUserId);

	void archiveOrUnarchive(int notePrefId, Status status, String loggedInUserId);

	void trashOrRestore(int notePrefId, Status status, String loggedInUserId);

}
