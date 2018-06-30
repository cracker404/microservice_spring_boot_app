package com.fundoonote.msnoteservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ESNotePreferences implements Serializable {

	private static final long serialVersionUID = 1L;

	public ESNotePreferences() {	}

	public ESNotePreferences(NotePreferences preferences) {
		this.esNotePreId=preferences.getNotePreId();
		this.color = preferences.getColor();
		this.remainder = preferences.getRemainder();
		this.status = preferences.getStatus();
		this.isPin = preferences.isPin();
		this.labelIds = preferences.getLabels().stream().map(l -> {
			return l.getLabelId();
		}).collect(Collectors.toSet());
		this.noteId=preferences.getNote().getNoteId();
		this.userId = preferences.getUserId();
	}

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

	@Override
	public String toString() {
		return "ESNotePreferencesDto [esNotePreId=" + esNotePreId + ", status=" + status + ", isPin=" + isPin
				+ ", color=" + color + ", remainder=" + remainder + ", labelIds=" + labelIds + ", noteId=" + noteId
				+ ", userId=" + userId + "]";
	}

	public void copy(NotePreferences preferences) {
		this.color = preferences.getColor();
		this.remainder = preferences.getRemainder();
		this.status = preferences.getStatus();
		this.isPin = preferences.isPin();
		this.labelIds = preferences.getLabels().stream().map(l -> {
			return l.getLabelId();
		}).collect(Collectors.toSet());
	}
}