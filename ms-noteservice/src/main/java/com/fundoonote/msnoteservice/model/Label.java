package com.fundoonote.msnoteservice.model;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Label {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int LabelId;
	
	@Column
	private String name;
	
	@Column
	private Integer userId;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "labels")
	private Set<NotePreferences> notePref;

	

	public int getLabelId() {
		return LabelId;
	}

	public void setLabelId(int labelId) {
		LabelId = labelId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId2) {
		this.userId = userId2;
	}

}
