package com.fundoonote.msnoteservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	@RequestMapping(value="/save", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<?> saveNote(@RequestBody NoteDto note, @RequestParam String userId) throws NSException{
		
		noteService.saveNote(note, userId);
		return new ResponseEntity<String>("Note created..." ,HttpStatus.OK);	
	}
	
	@RequestMapping(value="/updateNote", method = RequestMethod.PUT,produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> updateNote(@RequestBody Note note, HttpServletRequest request){
		
		Response response = new Response();
		noteService.updateNote(note);
		response.setStatusCode(200);
		response.setResponseMessage("Note updated...");
		return new ResponseEntity<Response>(response,HttpStatus.OK);	
	}
	
	@RequestMapping(value="/updateNotePref", method = RequestMethod.PUT,produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> updateNotePref(@RequestBody NotePreferences notePref, HttpServletRequest request){
		
		Response response = new Response();
		noteService.updatenotePref(notePref);
		response.setStatusCode(200);
		response.setResponseMessage("Note added...");
		return new ResponseEntity<Response>(response,HttpStatus.OK);	
	}
	
	@RequestMapping(value="/deletenote/{noteId}", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteNote(@PathVariable int noteId){
		Response response = new Response();
		noteService.deleteNote(noteId);
		response.setStatusCode(200);
		response.setResponseMessage("note deleted successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/getnotes", method = RequestMethod.GET)
	ResponseEntity<List<NoteDto>> getNotes(@RequestParam String loggedInUser){
		List<NoteDto> notes = noteService.getNotes(loggedInUser);
		return new ResponseEntity<List<NoteDto>>(notes, HttpStatus.OK);
	}
	
	@RequestMapping(value="/label/save", method = RequestMethod.POST)
	ResponseEntity<String> saveLabel(@RequestBody Label label,  @RequestParam String loggedInUserId){
		Response response = new Response();
		noteService.saveLabel(label,loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("label saved successfully");
		return new ResponseEntity<String>("Note deleted succesfully", HttpStatus.OK);
	}
	
	@RequestMapping(value="/label/renamelabel", method = RequestMethod.PUT)
	ResponseEntity<Response> renameLabel(@RequestBody Label label,  @RequestParam String loggedInUserId){
		
		Response response = new Response();
		noteService.renameLabel(label, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("label updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@RequestMapping(value="/label/deletelabel{labelId}", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteLabel(@PathVariable int labelId){
		
		Response response = new Response();
		noteService.deleteLabel(labelId);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/label/addremovelabel", method = RequestMethod.POST)
	ResponseEntity<Response> addRemoveLabel(@RequestParam int noteId, @RequestParam int labelId){
		
		Response response = new Response();
		noteService.addRemoveLabel(noteId, labelId);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/label/saveLabelFromNote", method = RequestMethod.POST)
	ResponseEntity<Response> saveLabelFromNote(@RequestBody Label label, @RequestParam int noteId, @RequestParam String loggedInUserId){
		
		Response response = new Response();
		noteService.saveLabelFromNote(label, noteId, loggedInUserId);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/saveimage", method = RequestMethod.POST)
	ResponseEntity<Response> saveImage(@RequestPart MultipartFile image, int noteId){
		
		Response response = new Response();
		noteService.saveImage(image, noteId);
		response.setStatusCode(200);
		response.setResponseMessage("imgae uploaded successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/deleteimage", method = RequestMethod.DELETE)
	ResponseEntity<Response> deleteImage(@RequestParam("id") int userId, @RequestParam("noteId") int noteId,
	         @RequestParam("key") String key){
		Response response = new Response();
		noteService.deleteImage(userId, noteId, key);
		response.setStatusCode(200);
		response.setResponseMessage("imgae deleted successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/collaborate", method = RequestMethod.POST)
	ResponseEntity<Response> collaborate(@RequestParam String sharingUserEmail, @RequestParam int noteId, @RequestParam String loggedInUserEmail ){
		
		Response response = new Response();
		noteService.collaborat(sharingUserEmail, noteId, loggedInUserEmail);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/removecollaborate", method = RequestMethod.DELETE)
	ResponseEntity<Response> removeCollaborate(){
		
		Response response = new Response();
	//	noteService.saveLabelFromNote(label, noteId, loggedInUserId);
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/pinorunpin", method = RequestMethod.PUT)
	ResponseEntity<Response> pinOrUnpin(@RequestParam int notePrefId, @RequestParam boolean isPinned, @RequestParam String loggedInUserId){
		
		Response response = new Response();
		noteService.pinOrUnpin(notePrefId, isPinned, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/archiveorunarchive", method = RequestMethod.PUT)
	ResponseEntity<Response> archiveOrUnarchive(@RequestParam int notePrefId, @RequestParam Status status, @RequestParam String loggedInUserId){
		
		Response response = new Response();
		noteService.archiveOrUnarchive(notePrefId, status, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/trashorrestore", method = RequestMethod.PUT)
	ResponseEntity<Response> trashOrRestore(@RequestParam int notePrefId, @RequestParam Status status, @RequestParam String loggedInUserId){
		
		Response response = new Response();
		noteService.trashOrRestore(notePrefId, status, loggedInUserId);
		response.setStatusCode(200);
		response.setResponseMessage("data updated successfully");
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
}
