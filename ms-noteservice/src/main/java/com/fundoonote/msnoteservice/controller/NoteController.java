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

@RestController
@RequestMapping("/notes")
public class NoteController {

	@Autowired
	INoteService noteService;

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> saveNote(@RequestBody NoteDto note, @RequestParam("userid") String userId) throws NSException {
		Response response = new Response();
		noteService.saveNote(note, userId);
		response.setStatusCode(200);
		response.setResponseMessage("Note created...");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatenote", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> updateNote(@RequestBody Note note, @RequestParam("userid") String userId)
			throws NSException {

		Response response = new Response();
		noteService.updateNote(note, userId);
		response.setStatusCode(200);
		response.setResponseMessage("Note updated...");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateNotePref", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> updateNotePref(@RequestBody NotePreferences notePref) {

		Response response = new Response();
		noteService.updatenotePref(notePref);
		response.setStatusCode(200);
		response.setResponseMessage("Note added...");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deletenote/{noteId}", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteNote(@PathVariable int noteId) throws NSException {
		Response response = new Response();
		noteService.deleteNote(noteId);
		response.setStatusCode(200);
		response.setResponseMessage("note deleted successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/getnotes", method = RequestMethod.GET)
	ResponseEntity<List<NoteDto>> getNotes(@RequestParam("userid") String loggedInUserId) {
		List<NoteDto> notes = noteService.getNotes(loggedInUserId);
		return new ResponseEntity<List<NoteDto>>(notes, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/save", method = RequestMethod.POST)
	ResponseEntity<Response> saveLabel(@RequestBody Label label, @RequestParam("userid") String loggedInUserId) {
		Response response = new Response();
		noteService.saveLabel(label, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("label saved successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/renamelabel", method = RequestMethod.PUT)
	ResponseEntity<Response> renameLabel(@RequestBody Label label, @RequestParam("userid") String loggedInUserId) {

		Response response = new Response();
		noteService.renameLabel(label, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("label updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/deletelabel/{labelId}", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteLabel(@PathVariable int labelId) {

		Response response = new Response();
		noteService.deleteLabel(labelId);
		response.setResponseMessage("Label deleted successfully");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/getlabels", method = RequestMethod.GET)
	ResponseEntity<List<Label>> getLabels(@RequestParam("userid") String loggedInUser) {
		List<Label> labels = noteService.getLabels(loggedInUser);
		return new ResponseEntity<List<Label>>(labels, HttpStatus.OK);

	}

	/*@RequestMapping(value = "/label/addremovelabel", method = RequestMethod.POST)
	ResponseEntity<Response> addRemoveLabel(@RequestHeader int noteId, @RequestHeader int labelId) {

		Response response = new Response();
		noteService.addLabel(noteId, labelId);
		response.setResponseMessage("Label is added to note");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/label/saveLabelFromNote", method = RequestMethod.POST)
	ResponseEntity<Response> saveLabelFromNote(@RequestBody Label label, @RequestHeader int noteId,
			@RequestParam("userid") String loggedInUserId) {

		Response response = new Response();
		noteService.saveLabelFromNote(label, noteId, loggedInUserId);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}*/

	@RequestMapping(value = "/saveimage", method = RequestMethod.POST)
	ResponseEntity<Response> saveImage(@RequestPart MultipartFile image, int noteId) {

		Response response = new Response();
		noteService.saveImage(image, noteId);
		response.setStatusCode(200);
		response.setResponseMessage("imgae uploaded successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteimage", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteImage(@RequestParam("userid") int userId, @RequestHeader("noteId") int noteId,
			@RequestHeader("key") String key) {
		Response response = new Response();
		noteService.deleteImage(userId, noteId, key);
		response.setStatusCode(200);
		response.setResponseMessage("imgae deleted successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/collaborate", method = RequestMethod.POST)
	ResponseEntity<Response> collaborate(@RequestHeader String sharingUserEmail, @RequestHeader int noteId,
			@RequestParam("userid") String loggedInUserEmail) {

		Response response = new Response();
		noteService.collaborate(sharingUserEmail, noteId, loggedInUserEmail);
		response.setResponseMessage("Note is Collaborated Successfully");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/removecollaborate", method = RequestMethod.DELETE)
	ResponseEntity<Response> removeCollaborate(@RequestHeader String sharedUserId, @RequestHeader long noteId) {

		Response response = new Response();
		noteService.removeCollaborator(sharedUserId, noteId);
		response.setResponseMessage("Collaborator is deleted successfully");
		response.setStatusCode(200);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/pinorunpin", method = RequestMethod.PUT)
	ResponseEntity<Response> pinOrUnpin(@RequestHeader long notePrefId, @RequestHeader boolean isPinned,
			@RequestParam("userid") String loggedInUserId) {

		Response response = new Response();
		noteService.pinOrUnpin(notePrefId, isPinned, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/archiveorunarchive", method = RequestMethod.PUT)
	ResponseEntity<Response> archiveOrUnarchive(@RequestHeader long notePrefId, @RequestHeader Status status,
			@RequestParam("userid") String loggedInUserId) {

		Response response = new Response();
		noteService.archiveOrUnarchive(notePrefId, status, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/trashorrestore", method = RequestMethod.PUT)
	ResponseEntity<Response> trashOrRestore(@RequestHeader long notePrefId, @RequestHeader Status status,
			@RequestParam("userid") String loggedInUserId) {

		Response response = new Response();
		noteService.trashOrRestore(notePrefId, status, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
}
