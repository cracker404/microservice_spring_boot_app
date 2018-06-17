package com.bridgelabz.search.services;

import java.util.List;
import java.util.Map;

import com.bridgelabz.search.services.FNException;

public interface IESService {
	<T> String save(T object, String index, String id) throws FNException;

	<T> String update(T object, String index, String id) throws FNException;

	String getById(String id, String index) throws FNException;

	boolean deleteById(String id, String index) throws FNException;

	// ************MODIFIED*****************//
	// Returns list of objects matching given field, partial match is allowed
	List<String> multipleFieldSearchQuery(Map<String, Object> fieldValueMap, String index) throws FNException;

	// ************MODIFIED*****************//
	// Returns list of objects matching given field, partial match is allowed
	List<String> filteredQuery(String field, Object value, String index) throws FNException;

	// ************MODIFIED*****************//
	// wildcard search with restrictions(such as user id) and field priority
	List<String> multipleFieldSearchWithWildcard(String text, Map<String, Float> fields,
			Map<String, Object> restrictions, String index) throws FNException;

}
