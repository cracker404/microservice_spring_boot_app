package com.fundoonote.msnoteservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msnoteservice.dao.ICollaboratorDao;
import com.fundoonote.msnoteservice.dao.ILabeDao;
import com.fundoonote.msnoteservice.dao.INoteDao;
import com.fundoonote.msnoteservice.dao.INotePrefDao;
import com.fundoonote.msnoteservice.exception.NSException;
import com.fundoonote.msnoteservice.model.Collaboration;
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;
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

	/*
	 * @Autowired IUserDao userDao;
	 */

	@Autowired
	ICollaboratorDao collaboratorDao;

	@Autowired
	S3Service s3Service;

	@Override
	public void saveNote(NoteDto noteDto, String userId) throws NSException {

		String imageUrl = null;
		if (noteDto.getImage() != null) {
			imageUrl = s3Service.saveImageToS3(noteDto.getNote().getNoteId(), noteDto.getImage());
		}
		Note note = noteDto.getNote();
		note.setUserId(userId);
		note.setCreatedDate(new Date());
		note.setLastUpdated(new Date());
		noteDao.save(note);
		jmsService.addToQueue(note, OperationType.SAVE);
		NotePreferences notePref = noteDto.getNotePreferences();
		notePref.setNote(note);
		notePref.setUserId(userId);
		notePrefDao.save(notePref);
		jmsService.addToQueue(notePref, OperationType.SAVE);

		if (noteDto.getNotePreferences().getLabels() != null) {
			Set<Label> labels = noteDto.getNotePreferences().getLabels();
			for (Label label : labels) {
				label.setUserId(userId);
				labelDao.save(label);
			}
			notePref.setLabels(labels);
			notePref.setNote(note);
			notePref.setUserId(userId);
			notePrefDao.save(notePref);
		}

		if (noteDto.getCollaboratorId() != null) {
			Set<String> collaborators = noteDto.getCollaboratorId();
			for (String collaboratorid : collaborators) {
				Collaboration collaboration = new Collaboration();
				collaboration.setNote(note);
				collaboration.setSharedById(userId);
				collaboration.setSharedId(collaboratorid.toString());
				collaboratorDao.save(collaboration);
			}
		}
	}

	@Override
	public void updateNote(Note note, String userId) throws NSException {
		Note oldNote = noteDao.getOne(note.getNoteId());
		if (oldNote == null) {
			throw new NSException(124, new Object[] { "note.getId()" });
		}
		if (oldNote.getUserId() == userId) {
			throw new NSException(111, new Object[] { "update note " });
		}
		oldNote.setTitle(note.getTitle());
		oldNote.setBody(note.getBody());
		oldNote.setLastUpdated(new Date());
		noteDao.save(oldNote);
		jmsService.addToQueue(oldNote, OperationType.UPDATE);
	}

	@Override
	public void updatenotePref(NotePreferences notePref, String loggedInUserId) throws NSException {

		Optional<NotePreferences> oldNotePreferences = notePrefDao.findById(notePref.getNotePreId());
		if (!oldNotePreferences.isPresent()) {
			throw new NSException(124, new Object[] { "notePref.getNotePreId()" });
		}
		if (!oldNotePreferences.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "update note " });
		}
		notePrefDao.save(notePref);
		jmsService.addToQueue(notePref, OperationType.UPDATE);
	}

	@Override
	public void deleteNote(long noteId, String loggedInUserId) throws NSException {

		Optional<Note> note = noteDao.findById(noteId);
		if (!note.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "delete note :-" });
		}
		noteDao.deleteById(noteId);
		jmsService.addToQueue(noteId, OperationType.DELETE);
	}

	@Override
	public List<NoteDto> getNotes(String loggedInUser) {
        
	    List<NoteDto> noteDTOs = new ArrayList<NoteDto>();
		List<NotePreferences> notePreferences = notePrefDao.getAllNotePreferenceByUserId(loggedInUser);
		List<NoteDto> result = notePreferences.stream().map(temp -> {
			NoteDto noteDto = new NoteDto();
			noteDto.setNote(temp.getNote());
			noteDto.setCollaboratorId(getAllCollabUserByNote(temp.getNote().getNoteId()));
			noteDto.setNotePreferences(temp);
			return noteDto;
		}).collect(Collectors.toList());
		
		return result;
	}

	private Set<String> getAllCollabUserByNote(long noteId) {
		Note note = new Note();
		note.setNoteId(noteId);
		List<Collaboration> collaborators = collaboratorDao.getByNote(note);
		Set<String> sharedUserIds = collaborators.stream().map(temp -> temp.getSharedId()).collect(Collectors.toSet());
		return sharedUserIds;
	}

	@Override
	public void saveLabel(Label label, String loggedInUserId) throws NSException {
		Label OldLabel = labelDao.findByNameAndUserId(label.getName(), loggedInUserId);
		if (OldLabel != null) {
			throw new NSException(115, new Object[] { label.getName() });
		}
		label.setUserId(loggedInUserId);
		labelDao.save(label);
	}

	@Override
	public void renameLabel(Label label, String loggedInUserId) throws NSException {

		Optional<Label> oldLabel = labelDao.findById(label.getLabelId());
		if (!oldLabel.isPresent()) {
			throw new NSException(114, new Object[] { "label.getLabelId()" });
		}
		if (!oldLabel.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "Rename Label :-" });
		}
		Label labelFromDB = labelDao.getOne(label.getLabelId());
		labelFromDB.setName(label.getName());
		labelDao.save(labelFromDB);
	}

	@Override
	public List<Label> getLabels(String userId) {
		List<Label> labels = labelDao.getAllLabelsByUserId(userId);
		return labels;
	}

	@Override
	public void deleteLabel(int labelId, String loggedInUserId) throws NSException {

		Optional<Label> oldLabel = labelDao.findById(labelId);
		if (!oldLabel.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "Delete Label :-" });
		}
		labelDao.deleteById(labelId);
	}

	@Override
	public void addLabelToNote(long noteId, int labelId, String loggedInUserId) {

		Note note = new Note();
		note.setNoteId(noteId);
		NotePreferences notePreferences = notePrefDao.getByNote(note);
		Label label = labelDao.getOne(labelId);
		Set<Label> labels = new HashSet<Label>();
		notePreferences.setLabels(labels);
		notePrefDao.save(notePreferences);

	}

	@Override
	public void saveLabelFromNote(Label label, long noteId, String loggedInUserId) {

	}

	@Override
	public void deleteImage(int userId, long noteId, String key) {

		Note note = noteDao.getOne(noteId);
		s3Service.deleteFileFromS3(key);
		note.setImageUrl(null);
		noteDao.save(note);
	}

	@Override
	public void saveImage(MultipartFile image, long noteId) {

		Note note = noteDao.getOne(noteId);
		String imageUrl = s3Service.saveImageToS3(noteId, image);
		note.setImageUrl(imageUrl);
		noteDao.save(note);
	}

	@Override
	public void collaborate(String sharingUserEmail, long noteId, String loggedInUserEmail) {

		Collaboration collaboration = new Collaboration();
		Note note = noteDao.getOne(noteId);
		// User sharedUser = userDao.findByEmail(sharingUserEmail);

		collaboration.setNote(note);
		collaboration.setSharedById(loggedInUserEmail);
		collaboration.setSharedId(sharingUserEmail);
		collaboratorDao.save(collaboration);
	}

	@Override
	public void removeCollaborator(String sharedUserId, long noteId, String loggedInUserId) throws NSException {
		Note note = noteDao.getOne(noteId);
		if (note.getUserId() == loggedInUserId) {
			throw new NSException(101, new Object[] { "" });
		}
		collaboratorDao.deleteByNoteAndSharedId(note, sharedUserId);
		note.setLastUpdated(new Date());
		noteDao.save(note);
	}

	@Override
	public void trashOrRestore(long notePrefId, Status status, String loggedInUserId) throws NSException {

		Optional<NotePreferences> notePref = notePrefDao.findById(notePrefId);
		if (!notePref.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "perform trash or Restore" });
		}
		if (status == null) {
			throw new NSException(123, new Object[] { "perform trash or Restore" });
		}
		NotePreferences notePreferences = notePref.get();
		notePreferences.setStatus(status);
		notePreferences.setUserId(loggedInUserId);
		notePrefDao.save(notePreferences);

	}

	@Override
	public void pinOrUnpin(long notePrefId, boolean isPinned, String loggedInUserId) throws NSException {

		Optional<NotePreferences> notePref = notePrefDao.findById(notePrefId);
		if (!notePref.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "perform pin or Unpin" });
		}
		NotePreferences notePreferences = notePref.get();
		notePreferences.setPin(isPinned);
		notePreferences.setUserId(loggedInUserId);
		notePrefDao.save(notePreferences);
	}

	@Override
	public void archiveOrUnarchive(long notePrefId, Status status, String loggedInUserId) throws NSException {

		Optional<NotePreferences> notePref = notePrefDao.findById(notePrefId);
		if (!notePref.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "perform archive or Unarchive" });
		}
		if (status == null) {
			throw new NSException(123, new Object[] { "perform archive or Unarchive" });
		}
		NotePreferences notePreferences = notePref.get();
		notePreferences.setStatus(status);
		notePreferences.setUserId(loggedInUserId);
		notePrefDao.save(notePreferences);

	}

}
