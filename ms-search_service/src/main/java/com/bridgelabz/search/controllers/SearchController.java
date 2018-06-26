package com.bridgelabz.search.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.search.exception.FNException;
import com.bridgelabz.search.response.Response;
import com.bridgelabz.search.services.IESService;

@RestController
public class SearchController {

	@Autowired
	private IESService esService;

	/*@GetMapping("/notes/search")
	public List<String> searchNotes(@RequestHeader String text, @RequestHeader String index,
			@RequestParam String userid) throws FNException {
		Map<String, Float> fields = new HashMap<>();
		fields.put("title", 2.0f);
		fields.put("description", 1.5f);

		Map<String, Object> restrictions = new HashMap<>();
		restrictions.put("userId", userid);

		return esService.multipleFieldSearchWithWildcard(text, fields, restrictions, index);
	}*/

	@GetMapping(value="/search")
	public List<String> search(@RequestHeader String text, @RequestHeader String index) throws FNException, IOException {
		return esService.searchByText(index, index, "*" + text + "*");
	}

	/*@GetMapping("/user/search")
	public List<String> searchUsers(@RequestHeader String text, @RequestHeader String index) throws FNException {

		Map<String, Float> fields = new HashMap<>();
		fields.put("email", 2.0f);
		fields.put("name", 1.5f);

		return esService.multipleFieldSearchWithWildcard(text, fields, null, index);
	}*/

	@PostMapping("/save")
	public ResponseEntity<Response> add(@RequestBody Map<String, Object> object, @RequestParam String index,
			@RequestParam String id) {
		Response response = new Response();
		try {
			esService.save(object, index, id);
		} catch (FNException e) {
			return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			FNException fn = new FNException(101, new Object[] { "User Registration - " + e.getMessage() }, e);
			return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response = new Response();
		response.setStatus(200);
		response.setResponseMessage("Registration successfull");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/update")
	public ResponseEntity<Response> update(@RequestBody Map<String, Object> object, @RequestParam String index,
			@RequestParam String id) {
		Response response = new Response();
		try {
			esService.update(object, index, id);
		} catch (FNException e) {
			return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			FNException fn = new FNException(101, new Object[] { "User Registration - " + e.getMessage() }, e);
			return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response = new Response();
		response.setStatus(200);
		response.setResponseMessage("Registration successfull");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public Boolean remove(@RequestParam String index, @RequestParam String id) throws FNException {
		return esService.deleteById(id, index);
	}

	@GetMapping("/get")
	public String getById(@RequestParam String index, @RequestParam String id) throws FNException {
		return esService.getById(id, index);
	}
}
