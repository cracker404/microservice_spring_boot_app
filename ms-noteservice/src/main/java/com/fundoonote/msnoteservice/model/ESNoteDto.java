package com.fundoonote.msnoteservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ESNoteDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private long esNoteId;
	
	private String title;
	
	private String body;
	
	private	 Date createdDate = new Date();
	
	private Date lastUpdated = new Date();
	
	private String imageUrl;
	
	private Integer userId;
	
	private String shareByUserId;

	private Set<Integer> notePreferences = new HashSet<>();

	public long getEsNoteId() {
		return esNoteId;
	}

	public void setEsNoteId(long esNoteId) {
		this.esNoteId = esNoteId;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getShareByUserId() {
		return shareByUserId;
	}

	public void setShareByUserId(String shareByUserId) {
		this.shareByUserId = shareByUserId;
	}

	public Set<Integer> getNotePreferences() {
		return notePreferences;
	}

	public void setNotePreferences(Set<Integer> notePreferences) {
		this.notePreferences = notePreferences;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
