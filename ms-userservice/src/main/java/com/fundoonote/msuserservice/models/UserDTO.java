package com.fundoonote.msuserservice.models;

public class UserDTO 
{
	private Integer userId;
	private String name;
	private String email;
	private String picUrl;
	private String role;
	public UserDTO() {}
	
	public UserDTO(Integer userId, String name, String email, String picUrl, String role) {
		super();
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.picUrl = picUrl;
		this.role = role;
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "UserDTO [userId=" + userId + ", name=" + name + ", email=" + email + ", picUrl=" + picUrl + ", role="
				+ role + "]";
	}
	
}
