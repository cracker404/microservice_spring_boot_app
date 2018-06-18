package com.fundoonote.msnoteservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Note implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private long noteId;
	
	@NotNull
	@Column
	private String title;
	
	@NotNull
	@Column
	private String body;
	
	@Column
	private	 Date createdDate;
	
	@Column
	private Date lastUpdated;
	
	@Column
	private String imageUrl;
	
	@Column
	private String userId;
	
	@Column
	private String shareByUserId;
	
	/*@OneToMany(mappedBy = "note")
	private Set<NotePreferences> notePreferences;*/
	
	@OneToMany(mappedBy="note")
	private Set<Collaboration> collaborations;
	
	public long getNoteId() {
		return noteId;
	}
	public void setNoteId(long noteId) {
		this.noteId = noteId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getShareByUserId() {
		return shareByUserId;
	}
	public void setShareByUserId(String shareByUserId) {
		this.shareByUserId = shareByUserId;
	}
	/*public Set<NotePreferences> getNotePreferences() {
		return notePreferences;
	}
	public void setNotePreferences(Set<NotePreferences> notePreferences) {
		this.notePreferences = notePreferences;
	}*/
	public Set<Collaboration> getCollaborations() {
		return collaborations;
	}
	public void setCollaborations(Set<Collaboration> collaborations) {
		this.collaborations = collaborations;
	}
}
