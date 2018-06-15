package com.fundoonote.msnoteservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Collaboration {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private String id;
	
	@ManyToOne
	@JoinColumn(name="noteId")
	private Note note;
	
	@Column
	private String Shared_By_UserId;
	
	@Column
	private String Shared_UserId;
	
	@Column
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
		
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
	public String getShared_By_UserId() {
		return Shared_By_UserId;
	}
	public void setShared_By_UserId(String shared_By_UserId) {
		Shared_By_UserId = shared_By_UserId;
	}
	public String getShared_UserId() {
		return Shared_UserId;
	}
	public void setShared_UserId(String shared_UserId) {
		Shared_UserId = shared_UserId;
	}	
}