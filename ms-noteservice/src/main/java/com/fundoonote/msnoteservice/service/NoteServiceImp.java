package com.fundoonote.msnoteservice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msnoteservice.dao.ICollaboratorDao;
import com.fundoonote.msnoteservice.dao.ILabeDao;
import com.fundoonote.msnoteservice.dao.INoteDao;
import com.fundoonote.msnoteservice.dao.INotePrefDao;
import com.fundoonote.msnoteservice.exception.NSException;
import com.fundoonote.msnoteservice.messages.IJmsService;
import com.fundoonote.msnoteservice.messages.OperationType;
import com.fundoonote.msnoteservice.model.Collaboration;
import com.fundoonote.msnoteservice.model.ESNotePreferences;
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;

/**
 * <p>
 * This is a service layer for Note With {@link Service @Service}, we have added
 * all general purpose methods here those method will be invoked from
 * NoteController by passing appropriate parameters and will return a required
 * response.
 * </p>
 * 
 * @version 1
 * @since 2017-03-10
 * @author Bridgelabz
 */
@Service
public class NoteServiceImp implements INoteService {

	@Autowired
	private INoteDao noteDao;

	@Autowired
	private ILabeDao labelDao;

	@Autowired
	private IJmsService jmsService;

	@Autowired
	private INotePrefDao notePrefDao;

	@Autowired
	private ICollaboratorDao collaboratorDao;

	@Autowired
	private S3Service s3Service;

	@Override
	public void saveNote(@RequestBody NoteDto noteDto, Integer loggedInUserId) throws NSException {
		String imageUrl = null;
		if (noteDto.getImage() != null) {
			imageUrl = s3Service.saveImageToS3(noteDto.getNote().getNoteId(), noteDto.getImage());
		}
		Note note = noteDto.getNote();
		note.setImageUrl(imageUrl);
		note.setUserId(loggedInUserId);
		noteDao.save(note);
		jmsService.addToQueue(note, OperationType.SAVE, note.getNoteId());

		NotePreferences notePref = noteDto.getNotePreferences();
		saveNotePrefFromNote(notePref, note, loggedInUserId);

		Set<Integer> collabUserIds = noteDto.getCollaboratorId();
		for (Integer id : collabUserIds) {
			Collaboration collaboration = new Collaboration();
			collaboration.setNote(note);
			collaboration.setSharedById(loggedInUserId);
			collaboration.setSharedId(id);
			collaboratorDao.save(collaboration);
			jmsService.addToQueue(collaboration, OperationType.SAVE, collaboration.getId());
			saveNotePrefFromNote(new NotePreferences(), note, id);
		}
	}

	private void saveNotePrefFromNote(NotePreferences notePreferences, Note note, Integer userId) throws NSException {
		notePreferences.setNote(note);
		notePreferences.setUserId(userId);
		notePrefDao.save(notePreferences);
		jmsService.addToQueue(notePreferences, OperationType.SAVE, notePreferences.getNotePreId());
	}

	@Override
	public void updateNote(Note note, Integer userId) throws NSException {
		Optional<Note> oldNote = noteDao.findById(note.getNoteId());

		if (!oldNote.isPresent() && !(oldNote.get().getUserId() == userId)) {
			throw new NSException(111, new Object[] { "" });
		}

		note.setLastUpdated(new Date());
		noteDao.save(note);
		jmsService.addToQueue(note, OperationType.UPDATE, note.getNoteId());
	}

	@Override
	public void updatenotePref(NotePreferences notePref, Integer loggedInUserId) throws NSException {

		Optional<NotePreferences> oldNotePreferences = notePrefDao.findById(notePref.getNotePreId());
		if (!oldNotePreferences.isPresent() && !oldNotePreferences.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "" });
		}

		notePrefDao.save(notePref);
		ESNotePreferences esNotePreferences = new ESNotePreferences(notePref);
		jmsService.addToQueue(esNotePreferences, OperationType.UPDATE, notePref.getNotePreId());
	}

	@Override
	public void deleteNote(int noteId, Integer loggedInUserId) throws NSException {

		Optional<Note> optional = noteDao.findById(noteId);
		if(!optional.isPresent())
			throw new NSException(111, new Object[] { "delete note :-" });
		Note note = optional.get();
		if (note.getUserId()!=loggedInUserId) {
			throw new NSException(111, new Object[] { "delete note :-" });
		}
		noteDao.deleteById(noteId);
		jmsService.addToQueue(note, OperationType.DELETE, noteId);
		
		NotePreferences preferences = notePrefDao.deleteByNoteAndUserId(note, loggedInUserId);
		jmsService.addToQueue(null, OperationType.DELETE, preferences.getNotePreId());

	}

	@Override
	public List<NoteDto> getNotes(Integer loggedInUser) {

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

	private Set<Integer> getAllCollabUserByNote(int noteId) 
	{
		Note note = new Note();
		note.setNoteId(noteId);
		List<Collaboration> collaborators = collaboratorDao.getByNote(note);
		Set<Integer> sharedUserIds = collaborators.stream().map(temp -> temp.getSharedId()).collect(Collectors.toSet());
		return sharedUserIds;
	}

	@Override
	public void saveLabel(Label label, Integer loggedInUserId) throws NSException {
		/*if (!(labelDao.findByNameAndUserId(label.getName(), loggedInUserId))) {
			throw new NSException(115, new Object[] { label.getName() });
		}*/
		label.setUserId(loggedInUserId);
		labelDao.save(label);
		jmsService.addToQueue(label, OperationType.SAVE, label.getLabelId());
	}

	@Override
	public void renameLabel(Label label, Integer loggedInUserId) throws NSException {
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
		jmsService.addToQueue(label, OperationType.UPDATE, label.getLabelId());
	}

	@Override
	public List<Label> getLabels(Integer userId) {
		return labelDao.getAllLabelsByUserId(userId);
	}

	@Override
	public void deleteLabel(int labelId, Integer loggedInUserId) throws NSException {
		Optional<Label> optional = labelDao.findById(labelId);

		if (!optional.isPresent())
			throw new NSException(111, new Object[] { "Delete Label :-" });
		Label label = optional.get();
		if (label.getUserId() != loggedInUserId) {
			throw new NSException(111, new Object[] { "Delete Label :-" });
		}
		labelDao.deleteById(labelId);
		
		jmsService.addToQueue(label, OperationType.DELETE, label.getLabelId());
	}

	/*
	 * @Override public void addLabelToNote(int noteId, int labelId, Integer
	 * loggedInUserId) throws NSException { NotePreferences notePreferences =
	 * notePrefDao.getByNoteAndUserId(new Note(noteId), loggedInUserId);
	 * Optional<Label> label = labelDao.findById(labelId);
	 * 
	 * if (!label.isPresent()) { throw new NSException(114, new Object[] {
	 * "add Label to note :-" }); }
	 * 
	 * Set<Label> labels = notePreferences.getLabels(); labels.add(label.get());
	 * notePreferences.setLabels(labels); notePrefDao.save(notePreferences);
	 * jmsService.addToQueue(notePreferences, OperationType.SAVE,
	 * notePreferences.getNotePreId());
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @Transactional public void removeLabelFromNote(Label label, int noteId,
	 * Integer loggedInUserId) throws NSException { NotePreferences notePreferences
	 * = notePrefDao.getByNoteAndUserId(new Note(noteId), loggedInUserId);
	 * Set<Label> labels = notePreferences.getLabels(); for (Label notePrefLabel :
	 * labels) { if (notePrefLabel.getLabelId() == label.getLabelId()) {
	 * if(labels.remove(label)) { notePreferences.setLabels(labels);
	 * notePrefDao.save(notePreferences); jmsService.addToQueue(notePreferences,
	 * OperationType.SAVE, notePreferences.getNotePreId()); break; } } } }
	 */

	@Override
	public void addOrRemoveLabelFromNote(int labelId, int noteId, Integer loggedInUserId) throws NSException {
		NotePreferences notePreferences = notePrefDao.getByNoteAndUserId(new Note(noteId), loggedInUserId);
		Optional<Label> optional = labelDao.findById(labelId);
		Label label = optional.get();
		Set<Label> labels = notePreferences.getLabels();
		if (!labels.isEmpty() && labels.contains(label)) {
			labels.remove(label);
			notePreferences.setLabels(labels);
			notePrefDao.save(notePreferences);
			ESNotePreferences esNotePreference = new ESNotePreferences(notePreferences);
			jmsService.addToQueue(esNotePreference, OperationType.UPDATE, notePreferences.getNotePreId());
			return;
		}

		labels.add(label);
		notePreferences.setLabels(labels);
		notePrefDao.save(notePreferences);
		ESNotePreferences esNotePreference = new ESNotePreferences(notePreferences);
		jmsService.addToQueue(esNotePreference, OperationType.UPDATE, notePreferences.getNotePreId());
	}

	@Override
	public void deleteImage(Integer loggedInUserId, int noteId, String key) throws NSException {

		Optional<Note> optional = noteDao.findById(noteId);
		Note note = optional.get();
		if (!(note.getUserId() == loggedInUserId)) {
			throw new NSException(101, new Object[] { "note.getId()" });
		}
		s3Service.deleteFileFromS3(key);
		note.setImageUrl(null);
		noteDao.save(note);
		jmsService.addToQueue(note, OperationType.SAVE, note.getNoteId());
	}

	@Override
	public void saveImage(MultipartFile image, int noteId, Integer loggedInUserId) throws NSException 
	{
		Optional<Note> optional = noteDao.findById(noteId);
		if(!optional.isPresent())
			throw new NSException(111, new Object[] { "performing save image for note" });
		Note note = optional.get();
		if (!(note.getUserId() == loggedInUserId)) {
			throw new NSException(111, new Object[] { "performing save image for note" });
		}
		String imageUrl = s3Service.saveImageToS3(noteId, image);
		note.setImageUrl(imageUrl);
		noteDao.save(note);
		jmsService.addToQueue(note, OperationType.SAVE, note.getNoteId());
	}

	@Transactional
	@Override
	public void collaborate(Integer sharedUserId, int noteId, Integer loggedInUserId) throws NSException 
	{
		Optional<Note> optional = noteDao.findById(noteId);
		if(!optional.isPresent())
			throw new NSException(111, new Object[] { "performing collaborate for note" });
		Note note = optional.get();
		if (!(note.getUserId() == loggedInUserId)) {
			throw new NSException(111, new Object[] { "performing collaborate for note" });
		}
		Set<Integer> collaboratorId = getAllCollabUserByNote(noteId);
		for (Integer collaborators : collaboratorId) {
			if (collaborators.equals(sharedUserId))
				throw new NSException(121, new Object[] { sharedUserId });
		}
		Collaboration collaboration = new Collaboration();
		collaboration.setNote(new Note(note.getNoteId()));//Changed for elastic search
		collaboration.setSharedById(loggedInUserId);
		collaboration.setSharedId(sharedUserId);
		collaboratorDao.save(collaboration);
		
		jmsService.addToQueue(collaboration, OperationType.SAVE, collaboration.getId());
		saveNotePrefFromNote(new NotePreferences(), note, sharedUserId);
	}

	@Transactional
	@Override
	public void removeCollaborator(Integer sharedUserId, int noteId, Integer loggedInUserId) throws NSException {
		Optional<Note> optional = noteDao.findById(noteId);

		Note note = optional.get();
		if (!optional.isPresent()) {
			throw new NSException(111, new Object[] { "perform remove collaboration" });
		}
		if(note.getUserId() != sharedUserId)
		{
			throw new NSException(111, new Object[] { "perform remove collaboration" });
		}
		Collaboration collaborator = collaboratorDao.deleteByNoteAndSharedId(optional.get(), sharedUserId);
		jmsService.addToQueue(collaborator, OperationType.DELETE, collaborator.getId());

		NotePreferences notePreferences = notePrefDao.deleteByNoteAndUserId(note, sharedUserId);
		jmsService.addToQueue(null, OperationType.DELETE, notePreferences.getNotePreId());

	}

	@Transactional
	@Override
	public void trashOrRestore(int noteId, Status status, Integer loggedInUserId) throws NSException
	{
		if (status == Status.TRASH) 
		{
			List<Collaboration> collaborators = collaboratorDao.getByNote(new Note(noteId));
			for (Collaboration collaboration : collaborators) {
				collaboratorDao.delete(collaboration);
				jmsService.addToQueue(collaboration, OperationType.DELETE, collaboration.getId());
				NotePreferences notePreferences = notePrefDao.deleteByNoteAndUserId(new Note(noteId),
						collaboration.getSharedId());
				jmsService.addToQueue(null, OperationType.DELETE, notePreferences.getNotePreId());
			}
		}

		NotePreferences notePreferences = notePrefDao.getByNoteAndUserId(new Note(noteId), loggedInUserId);
		if (notePreferences == null)
			throw new NSException(111, new Object[] { "perform trash or Restore" });

		notePreferences.setStatus(status);
		notePrefDao.save(notePreferences);
		ESNotePreferences esNotePreferences = new ESNotePreferences(notePreferences);
		jmsService.addToQueue(esNotePreferences, OperationType.UPDATE, notePreferences.getNotePreId());
	}

	@Override
	public void pinOrUnpin(int notePrefId, boolean isPinned, Integer loggedInUserId) throws NSException {

		Optional<NotePreferences> optional = notePrefDao.findById(notePrefId);
		
		if (!optional.isPresent()) {
			throw new NSException(111, new Object[] { "perform pin or Unpin" });
		}
		NotePreferences notePreferences = optional.get();

		if(notePreferences.getUserId() != loggedInUserId) {
			throw new NSException(111, new Object[] { "perform pin or Unpin" });
		}
		notePreferences.setPin(isPinned);
		notePrefDao.save(notePreferences);
		ESNotePreferences esNotePreferences = new ESNotePreferences(notePreferences);
		jmsService.addToQueue(esNotePreferences, OperationType.UPDATE, notePreferences.getNotePreId());
	}

	@Override
	public void archiveOrUnarchive(int notePrefId, Status status, Integer loggedInUserId) throws NSException {

		Optional<NotePreferences> optional = notePrefDao.findById(notePrefId);
		if(!optional.isPresent()) {
			throw new NSException(111, new Object[] { "perform archive or Unarchive" });
		}
		NotePreferences notePreferences = optional.get();

		if (notePreferences.getUserId() != loggedInUserId) {
			throw new NSException(111, new Object[] { "perform archive or Unarchive" });
		}
		notePreferences.setStatus(status);
		notePrefDao.save(notePreferences);
		ESNotePreferences esNotePreferences = new ESNotePreferences(notePreferences);
		jmsService.addToQueue(esNotePreferences, OperationType.UPDATE, notePreferences.getNotePreId());
	}

	@Transactional
	@Override
	public List<NoteDto> getNoteByStatus(Status status, Integer loggedInUser) {
		List<NotePreferences> notePreferences = notePrefDao.getAllNotePreferenceByUserIdAndStatus(loggedInUser, status);
		List<NoteDto> result = notePreferences.stream().map(temp -> {
			NoteDto noteDto = new NoteDto();
			noteDto.setNote(temp.getNote());
			noteDto.setCollaboratorId(getAllCollabUserByNote(temp.getNote().getNoteId()));
			noteDto.setNotePreferences(temp);
			return noteDto;
		}).collect(Collectors.toList());

		return result;
	}
}
