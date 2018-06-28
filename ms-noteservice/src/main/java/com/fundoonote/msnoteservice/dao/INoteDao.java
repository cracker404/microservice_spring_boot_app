package com.fundoonote.msnoteservice.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Note;

@Repository
public interface INoteDao extends CrudRepository<Note, Integer> {

}
