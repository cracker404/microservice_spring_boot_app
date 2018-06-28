package com.fundoonote.msnoteservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ESNotePreferencesDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long esNotePreId;

	private Status status = Status.NONE;
	
	private boolean isPin = false;
	
	private String color = "white";
	
	private Date remainder;
	
	private Set<Integer> labelIds = new HashSet<>();

	private int noteId;
	
	private Integer userId;

	public long getEsNotePreId() {
		return esNotePreId;
	}

	public void setEsNotePreId(long esNotePreId) {
		this.esNotePreId = esNotePreId;
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

	public Set<Integer> getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(Set<Integer> labelIds) {
		this.labelIds = labelIds;
	}

	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}