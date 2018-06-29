package com.fundoonote.msnoteservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msnoteservice.exception.NSException;
import com.fundoonote.msnoteservice.model.Label;
import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.model.NoteDto;
import com.fundoonote.msnoteservice.model.NotePreferences;
import com.fundoonote.msnoteservice.model.Status;
import com.fundoonote.msnoteservice.response.Response;
import com.fundoonote.msnoteservice.service.INoteService;

/**
 * <p>
 * This is a Rest Controller for Note With
 * {@link RestController @RestController}, we have added all general purpose
 * methods here those method will accept a rest request in JSON form and will
 * return a JSON response.
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
@RestController
public class NoteController {
	@Autowired
	private INoteService noteService;

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	ResponseEntity<?> saveNote(@RequestBody NoteDto note, @RequestHeader(name = "userId") Integer loggedInUserId)
			throws NSException {
		Response response = new Response();
		noteService.saveNote(note, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("Note created...");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatenote", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> updateNote(@RequestBody Note note, @RequestHeader(name = "userId") Integer loggedInUserId)
			throws NSException {
		Response response = new Response();
		noteService.updateNote(note, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("Note updated...");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatenotepref", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> updateNotePref(@RequestBody NotePreferences notePref,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.updatenotePref(notePref, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("Note Preferences updated...");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deletenote/{noteId}", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteNote(@PathVariable int noteId,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {
		Response response = new Response();
		noteService.deleteNote(noteId, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("note deleted successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getnotes", method = RequestMethod.GET)
	ResponseEntity<List<NoteDto>> getNotes(@RequestHeader(name = "userId") Integer loggedInUserId) {
		List<NoteDto> notes = noteService.getNotes(loggedInUserId);
		return new ResponseEntity<List<NoteDto>>(notes, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/save", method = RequestMethod.POST)
	ResponseEntity<Response> saveLabel(@RequestBody Label label, @RequestHeader(name = "userId") Integer loggedInUserId)
			throws NSException {
		Response response = new Response();
		noteService.saveLabel(label, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("label saved successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/renamelabel", method = RequestMethod.PUT)
	ResponseEntity<Response> renameLabel(@RequestBody Label label,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.renameLabel(label, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("label updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/deletelabel/{labelId}", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteLabel(@PathVariable int labelId,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.deleteLabel(labelId, loggedInUserId);
		response.setResponseMessage("Label deleted successfully");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/getlabels", method = RequestMethod.GET)
	ResponseEntity<List<Label>> getLabels(@RequestHeader(name = "userId") Integer loggedInUserId) {
		List<Label> labels = noteService.getLabels(loggedInUserId);
		return new ResponseEntity<List<Label>>(labels, HttpStatus.OK);

	}

	@RequestMapping(value = "/label/addorremovelabelfromnote", method = RequestMethod.POST)
	ResponseEntity<Response> addOrRemoveLabelFromNote(@RequestParam int labelId, @RequestParam int noteId,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		response.setResponseMessage("Label removed from note succesfully..!!");
		response.setStatusCode(200);
		noteService.addOrRemoveLabelFromNote(labelId, noteId, loggedInUserId);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveimage", method = RequestMethod.POST)
	ResponseEntity<Response> saveImage(@RequestPart MultipartFile image, int noteId,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.saveImage(image, noteId, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("imgae uploaded successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteimage", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteImage(@RequestHeader(name = "userId") Integer loggedInUserId,
			@RequestParam("noteId") int noteId, @RequestHeader("key") String key) throws NSException {
		Response response = new Response();
		noteService.deleteImage(loggedInUserId, noteId, key);
		response.setStatusCode(200);
		response.setResponseMessage("imgae deleted successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/collaborate", method = RequestMethod.POST)
	ResponseEntity<Response> collaborate(@RequestParam Integer sharedUserId, @RequestParam int noteId,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.collaborate(sharedUserId, noteId, loggedInUserId);
		response.setResponseMessage("Note is Collaborated Successfully");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/removecollaborate", method = RequestMethod.DELETE)
	ResponseEntity<Response> removeCollaborate(@RequestParam Integer sharedUserId, @RequestParam int noteId,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.removeCollaborator(sharedUserId, noteId, loggedInUserId);
		response.setResponseMessage("Collaborator is deleted successfully");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/pinorunpin", method = RequestMethod.PUT)
	ResponseEntity<Response> pinOrUnpin(@RequestParam int notePrefId, @RequestParam boolean isPinned,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		Response response = new Response();
		noteService.pinOrUnpin(notePrefId, isPinned, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/archiveorunarchive", method = RequestMethod.PUT)
	ResponseEntity<Response> archiveOrUnarchive(@RequestParam int notePrefId, @RequestParam Status status,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {

		if (status == Status.TRASH) {
			throw new NSException(123, new Object[] { "perform archive or unarchive" });
		}
		Response response = new Response();
		noteService.archiveOrUnarchive(notePrefId, status, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/trashorrestore", method = RequestMethod.PUT)
	ResponseEntity<Response> trashOrRestore(@RequestParam int noteId, @RequestParam Status status,
			@RequestHeader(name = "userId") Integer loggedInUserId) throws NSException {
		
		if (status == Status.ARCHIVE) {
			throw new NSException(123, new Object[] { "perform trash or Restore" });
		}
		Response response = new Response();
		noteService.trashOrRestore(noteId, status, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getnotebystatus", method = RequestMethod.GET)
	ResponseEntity<List<NoteDto>> getNoteByStatus(@RequestHeader(name = "userId") Integer loggedInUserId,
			@RequestParam Status status) {
		List<NoteDto> noteDto = noteService.getNoteByStatus(status, loggedInUserId);
		return new ResponseEntity<List<NoteDto>>(noteDto, HttpStatus.OK);
	}
}
