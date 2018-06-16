package com.fundoonote.msnoteservice.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msnoteservice.dao.ICollaboratorDao;
import com.fundoonote.msnoteservice.dao.ILabeDao;
import com.fundoonote.msnoteservice.dao.INoteDao;
import com.fundoonote.msnoteservice.dao.INotePrefDao;
import com.fundoonote.msnoteservice.dao.IUserDao;
import com.fundoonote.msnoteservice.exception.NSException;
import com.fundoonote.msnoteservice.model.Collaboration;
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;
import com.fundoonote.msnoteservice.model.User;
import com.fundoonote.msnoteservice.utility.OperationType;
import com.fundoonote.msnoteservice.utility.S3Service;
import com.fundoonote.msnoteservice.utility.messagesservice.IJmsService;

@Service
public class NoteServiceImp implements INoteService {

	@Autowired
	INoteDao noteDao;

	@Autowired
	ILabeDao labelDao;

	@Autowired
	IJmsService jmsService;
	
	@Autowired
	INotePrefDao notePrefDao;

	@Autowired
	IUserDao userDao;

	@Autowired
	ICollaboratorDao collaboratorDao;

	@Autowired
	S3Service s3Service;

	@Override
	public void saveNote(NoteDto noteDto, String userId) throws NSException {

		Note note = noteDto.getNote();
		note.setUserId(userId);
		noteDao.save(note);
		jmsService.addToQueue(note, OperationType.SAVE);
		NotePreferences notePref = noteDto.getNotePreferences();
		notePref.setNote(note);
		Date date = new Date();
		note.setCreatedDate(date);
		note.setLastUpdated(date);
		notePref.setUserId(userId);
		notePrefDao.save(notePref);
		jmsService.addToQueue(notePref, OperationType.SAVE);

	}

	@Override
	public void updateNote(Note note, String userId) throws NSException {
		Note oldNote = noteDao.getOne(note.getNoteId());
		oldNote.setTitle(note.getTitle());
		oldNote.setBody(note.getBody());
		Date updatedDate = new Date();
		oldNote.setLastUpdated(updatedDate);
		noteDao.save(oldNote);
		jmsService.addToQueue(oldNote, OperationType.UPDATE);
	}

	@Override
	public void updatenotePref(NotePreferences notePref) {

		//noteDao.save(notePref);
	}

	@Override
	public void deleteNote(long noteId) throws NSException {

		noteDao.deleteById(noteId);
		jmsService.addToQueue(noteId, OperationType.DELETE);
	}

	@Override
	public List<NoteDto> getNotes(String loggedInUser) {

		List<NotePreferences> notePreferences = notePrefDao.getAllNotePreferenceByUserId(loggedInUser);
		List<NoteDto> result = notePreferences.stream().map(temp -> {
			NoteDto noteDto = new NoteDto();
			noteDto.setNote(temp.getNote());
			noteDto.setNotePreferences(temp);
			return noteDto;
		}).collect(Collectors.toList());

		return result;
	}

	@Override
	public void saveLabel(Label label, String loggedInUserId) {
		label.setUserId(loggedInUserId);
		labelDao.save(label);
	}

	@Override
	public void renameLabel(Label label, String loggedInUserId) {

		Label labelFromDB = labelDao.getOne(label.getLabelId());
		labelFromDB.setName(label.getName());
		labelDao.save(labelFromDB);
	}

	@Override
	public List<Label> getLabels() {
		return null;
	}

	@Override
	public void deleteLabel(int labelId) {

		labelDao.deleteById(labelId);
	}

	@Override
	public void addRemoveLabel(long noteId, int labelId) {

		Note note = noteDao.getOne(noteId);
		Label label = labelDao.getOne(labelId);
	}

	@Override
	public void saveLabelFromNote(Label label, long noteId, String loggedInUserId) {

	}

	@Override
	public void deleteImage(int userId, long noteId, String key) {

		Note note = noteDao.getOne(noteId);
		s3Service.deleteFileFromS3(key);
		note.setImageUrl(null);
	}

	@Override
	public void saveImage(MultipartFile image, long noteId) {

		Note note = noteDao.getOne(noteId);
		String imageUrl = s3Service.saveImageToS3(noteId, image);
		note.setImageUrl(imageUrl);
		noteDao.save(note);
	}

	@Override
	public void collaborat(String sharingUserEmail, long noteId, String loggedInUserEmail) {

		Collaboration collaboration = new Collaboration();
		Note note = noteDao.getOne(noteId);
		User loggedInUser = userDao.findByEmail(loggedInUserEmail);
		User sharedUser = userDao.findByEmail(sharingUserEmail);

		collaboration.setNote(note);
		collaboration.setShared_By_UserId(sharedUser.getUserId());
		collaboration.setShared_UserId(loggedInUser.getUserId());
	}

	@Override
	public void removeCollaboratUser() {

	}

	@Override
	public void trashOrRestore(int notePrefId, Status status, String loggedInUserId) {

		NotePreferences notePref = notePrefDao.getOne(notePrefId);
		notePref.setStatus(status);
		notePref.setUserId(loggedInUserId);
		notePrefDao.save(notePref);

	}

	@Override
	public void pinOrUnpin(int notePrefId, boolean isPinned, String loggedInUserId) {

		NotePreferences notePref = notePrefDao.getOne(notePrefId);
		notePref.setPin(isPinned);
		notePref.setUserId(loggedInUserId);
		notePrefDao.save(notePref);
	}

	@Override
	public void archiveOrUnarchive(int notePrefId, Status status, String loggedInUserId) {

		NotePreferences notePref = notePrefDao.getOne(notePrefId);
		notePref.setStatus(status);
		notePref.setUserId(loggedInUserId);
		notePrefDao.save(notePref);

	}

}
