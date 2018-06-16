package com.fundoonote.msnoteservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class NotePreferences implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long notePreId;
	
	@Column
	private Status status = Status.NONE;
	
	@Column
	private boolean isPin = false;
	
	@Column
	private String color = "white";
	
	@Column
	private Date remainder;
	
	@ManyToMany
	@JoinTable(name ="NoteLabel", joinColumns=@JoinColumn(name="noteId"), inverseJoinColumns=@JoinColumn(name="labelId"))
	private Set<Label> labels;
	
	@ManyToOne
	@JoinColumn(name="noteId")
	private Note note;
	
	@Column
	private String userId;
	
	public long getNotePreId() {
		return notePreId;
	}
	public void setNotePreId(long notePreId) {
		this.notePreId = notePreId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public boolean isPin() {
		return isPin;
	}
	public void setPin(boolean isPin) {
		this.isPin = isPin;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public Date getRemainder() {
		return remainder;
	}
	public void setRemainder(Date remainder) {
		this.remainder = remainder;
	}
	/*public Set<Label> getLabels() {
		return labels;
	}
	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}*/
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
