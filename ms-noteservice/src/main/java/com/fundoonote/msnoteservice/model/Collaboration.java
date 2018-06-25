package com.fundoonote.msnoteservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Collaboration {

	@Id
	@GenericGenerator(name = "any", strategy = "increment")
	@GeneratedValue(generator = "any")
	private long id;

	@ManyToOne
	@JoinColumn(name = "noteId")
	private Note note;

	@Column
	private Integer sharedById;

	@Column
	private String sharedId;

	@Column
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public Integer getSharedById() {
		return sharedById;
	}

	public void setSharedById(Integer userId) {
		this.sharedById = userId;
	}

	public String getSharedId() {
		return sharedId;
	}

	public void setSharedId(String sharedId) {
		this.sharedId = sharedId;
	}

}