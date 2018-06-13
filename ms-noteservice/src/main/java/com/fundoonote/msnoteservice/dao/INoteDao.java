package com.fundoonote.msnoteservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fundoonote.msnoteservice.model.Note;

@Repository
public interface INoteDao extends JpaRepository<Note, Long>{

}
