package com.fundoonote.msnoteservice.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;

/**
 * <p>
 * This is a service layer for Note With
 * {@link Service @Service}, we have added all general purpose
 * methods here those method will be invoked from NoteController 
 * by passing appropriate parameters and will return a required response.
 * </p>
 * <p>
 * The methods are self explanatory we have used <b>{@code @RestController}</b>
 * annotation to point incoming requests to this class, and
 * <b>{@link ResponseBody @ResponseBody}</b> annotation to point incoming
 * requests to appropriate Methods. <b>{@link RequestBody @RequestBody}</b>
 * annotation is used to accept data with request in JSON form and Spring
 * ResponseEntity is used to return JSON as response to incoming request.
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
	public void saveNote(@RequestBody NoteDto noteDto, Integer loggedInUserId) throws NSException 
	{
		String imageUrl = null;
		if (noteDto.getImage() != null) 
		{
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
		jmsService.addToQueue(notePref, OperationType.UPDATE, notePref.getNotePreId());
	}

	@Override
	public void deleteNote(int noteId, Integer loggedInUserId) throws NSException {

		Optional<Note> optional = noteDao.findById(noteId);
		if(!optional.isPresent())
			throw new NSException(111, new Object[] { "delete note :-" });
		Note note = optional.get();
		if (note.getUserId()==loggedInUserId) {
			throw new NSException(111, new Object[] { "delete note :-" });
		}
		noteDao.deleteById(noteId);
		jmsService.addToQueue(note, OperationType.DELETE, noteId);
		
		NotePreferences preferences = notePrefDao.deleteByNoteAndUserId(note, loggedInUserId);
		jmsService.addToQueue(preferences, OperationType.DELETE, preferences.getNotePreId());
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
	public void saveLabel(Label label, Integer loggedInUserId) throws NSException 
	{
		Label OldLabel = labelDao.findByNameAndUserId(label.getName(), loggedInUserId);
		if (OldLabel != null) 
		{
			throw new NSException(115, new Object[] { label.getName() });
		}
		label.setUserId(loggedInUserId);
		labelDao.save(label);
		jmsService.addToQueue(label, OperationType.SAVE, label.getLabelId());
	}

	@Override
	public void renameLabel(Label label, Integer loggedInUserId) throws NSException 
	{
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
	public List<Label> getLabels(Integer userId) 
	{
		return labelDao.getAllLabelsByUserId(userId);
	}

	@Override
	public void deleteLabel(int labelId, Integer loggedInUserId) throws NSException 
	{
		Optional<Label> optional = labelDao.findById(labelId);
		
		if(optional.isPresent())
			throw new NSException(111, new Object[] { "Delete Label :-" });
		Label label = optional.get();
		if (label.getUserId() == loggedInUserId) {
			throw new NSException(111, new Object[] { "Delete Label :-" });
		}
		labelDao.deleteById(labelId);
		
		jmsService.addToQueue(label, OperationType.DELETE, label.getLabelId());
	}

	@Override
	public void addLabelToNote(int noteId, int labelId, Integer loggedInUserId) throws NSException 
	{
		Optional<Note> note = noteDao.findById(noteId);
		if (!note.isPresent()) {
			throw new NSException(124, new Object[] { "add Label to note" });
		}
		if (!note.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "add Label to note :-" });
		}
		NotePreferences notePreferences = notePrefDao.getByNote(note.get());
		Optional<Label> label = labelDao.findById(labelId);
		if (!label.isPresent()) {
			throw new NSException(114, new Object[] { "add Label to note :-" });
		}
		if (!label.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "add Label to note :-" });
		}

		Set<Label> labels = new HashSet<Label>();
		labels.add(label.get());
		notePreferences.setLabels(labels);
		notePrefDao.save(notePreferences);
	}

	@Override
	@Transactional
	public void removeLabelFromNote(Label label, int noteId, Integer loggedInUserId) throws NSException {
		Optional<Note> note = noteDao.findById(noteId);
		if (note == null) {
			throw new NSException(124, new Object[] { "add Label to note" });
		}
		if (!note.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "add Label to note :-" });
		}
		NotePreferences notePreferences = notePrefDao.getByNote(note.get());
		Set<Label> labels = notePreferences.getLabels();
		if (!labels.isEmpty()) {
			for (Label notePrefLabel : labels) {
				if (notePrefLabel.getLabelId() == label.getLabelId())
					labels.remove(label);
				System.out.println(labels.size() + "" + labels.isEmpty());
			}
		}
		notePreferences.setLabels(labels);
		notePrefDao.save(notePreferences);

	}

	@Override
	public void deleteImage(Integer loggedInUserId, int noteId, String key) throws NSException {

		Optional<Note> note = noteDao.findById(noteId);
		if (!(note.get().getUserId() == loggedInUserId)) {
			throw new NSException(101, new Object[] { "note.getId()" });
		}
		s3Service.deleteFileFromS3(key);
		note.get().setImageUrl(null);
		noteDao.save(note.get());
	}

	@Override
	public void saveImage(MultipartFile image, int noteId, Integer loggedInUserId) throws NSException {

		Optional<Note> note = noteDao.findById(noteId);
		if (!(note.get().getUserId() == loggedInUserId)) {
			throw new NSException(101, new Object[] { "" });
		}
		String imageUrl = s3Service.saveImageToS3(noteId, image);
		if (imageUrl == null) {
			throw new NSException(108, new Object[] { "" });
		}
		note.get().setImageUrl(imageUrl);
		noteDao.save(note.get());
	}

	@Override
	public void collaborate(Integer sharingUserEmail, int noteId, Integer loggedInUserId) throws NSException {

		Optional<Note> note = noteDao.findById(noteId);
		if (!(note.get().getUserId() == loggedInUserId)) {
			throw new NSException(101, new Object[] { "" });
		}
		Set<Integer> collaboratorId = getAllCollabUserByNote(noteId);
		for (Integer collaborators : collaboratorId) {
			if (collaborators.equals(sharingUserEmail))
				throw new NSException(121, new Object[] { "" });
		}
		Collaboration collaboration = new Collaboration();
		collaboration.setNote(note.get());
		collaboration.setSharedById(loggedInUserId);
		collaboration.setSharedId(sharingUserEmail);
		collaboratorDao.save(collaboration);
	}

	@Override
	public void removeCollaborator(Integer sharedUserId, int noteId, Integer loggedInUserId) throws NSException
	{
		Optional<Note> optional = noteDao.findById(noteId);

		if (!optional.isPresent() && !(optional.get().getUserId() == loggedInUserId)) {
			throw new NSException(111, new Object[] { "" });
		}
		collaboratorDao.deleteByNoteAndSharedId(optional.get(), sharedUserId);
	}

	@Override
	public void trashOrRestore(int notePrefId, Status status, Integer loggedInUserId) throws NSException 
	{
		if( status != Status.TRASH || status != Status.NONE )
	    {
			throw new NSException(123, new Object[] { "perform trash or Restore" });
	    }
		Optional<NotePreferences> notePref = notePrefDao.findById(notePrefId);
		if (!notePref.get().getUserId().equals(loggedInUserId)) {
			throw new NSException(111, new Object[] { "perform trash or Restore" });
		}
		
		NotePreferences notePreferences = notePref.get();
		notePreferences.setStatus(status);
		notePreferences.setUserId(loggedInUserId);
		notePrefDao.save(notePreferences);
	}

	@Override
	public void pinOrUnpin(int notePrefId, boolean isPinned, Integer loggedInUserId) throws NSException {

		
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
	public void archiveOrUnarchive(int notePrefId, Status status, Integer loggedInUserId) throws NSException {

		if( status != Status.ARCHIVE || status != Status.NONE )
	    {
			throw new NSException(123, new Object[] { "perform trash or Restore" });
	    }
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
