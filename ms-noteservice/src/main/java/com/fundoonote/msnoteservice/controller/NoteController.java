package com.fundoonote.msnoteservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fundoonote.msnoteservice.model.Note;
import com.fundoonote.msnoteservice.service.INoteService;

@RestController
public class NoteController {

	private INoteService iNoteService;
	
	@Autowired
	public NoteController(INoteService iNoteService) {
		this.iNoteService = iNoteService;
	}
	
	
	
	@RequestMapping(method = RequestMethod.GET, path = "/notes")
    public ResponseEntity<Iterable<Note>> getAll() {
		/*System.out.println("IN Inside Notes**********************************************************");
		RestTemplate restTemplate = new RestTemplate();
        User user = restTemplate.getForObject("http://localhost:8081/user/users", User.class);
        System.out.println("User from Other:"+ user);*/
        Iterable<Note> allNote = iNoteService.findAll();

        return new ResponseEntity<>(allNote, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/register")
    public ResponseEntity<Note> register(@RequestBody Note note) {
    	System.out.println("Inside ");

        Note result = iNoteService.createNote(note);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
