package com.bridgelabz.search.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.search.services.FNException;
import com.bridgelabz.search.services.IESService;

@RestController
public class SearchController {

	@Autowired
	IESService esService;

	@GetMapping("/notes/search")
	public List<String> searchNotes(@RequestHeader String text, @RequestHeader String index, @RequestParam String userid)
			throws FNException {
		Map<String, Float> fields = new HashMap<>();
		fields.put("title", 2.0f);
		fields.put("description", 1.5f);

		Map<String, Object> restrictions = new HashMap<>();
		restrictions.put("userId", userid);

		return esService.multipleFieldSearchWithWildcard(text, fields, restrictions, index);
	}

	@GetMapping("/user/search")
	public List<String> searchUsers(@RequestHeader String text, @RequestHeader String index)
			throws FNException {

		Map<String, Float> fields = new HashMap<>();
		fields.put("email", 2.0f);
		fields.put("name", 1.5f);
		
		return esService.multipleFieldSearchWithWildcard(text, fields, null, index);
	}
	
	@PostMapping("/add")
	public String add(@RequestBody Map<String, Object> object, @RequestHeader String index, @RequestHeader String id) throws FNException {
		return esService.save(object, index, id);
	}
	
	@PutMapping("/update")
	public String update(@RequestBody Map<String, Object> object, @RequestHeader String index, @RequestHeader String id) throws FNException {
		return esService.update(object, index, id);
	}
	
	@DeleteMapping("/delete")
	public boolean remove(@RequestHeader String index, @RequestHeader String id) throws FNException {
		return esService.deleteById(id, index);
	}
	
	@GetMapping("/get")
	public String getById(@RequestHeader String index, @RequestHeader String id) throws FNException {
		return esService.getById(id, index);
	}
	
}
