package com.fundoonote.msuserservice.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "user", strategy = "increment")
	@GeneratedValue(generator = "user")
	private int userId;
	@NotNull(message = "*Please provide a Name")
	@Size(min = 3, message = "*name must have at least 3 characters")
	private String name;

	@NotNull(message = "*Please provide an email")
	@javax.validation.constraints.Email(message = "*Please provide a valid Email")
	private String email;

	@NotNull(message = "*Please Provide Password")
	@Size(min = 3, message = "*Password must have at least 3 character")
	@Pattern(groups = Pattern.class, regexp = "^((?=.*\\d)(?=.*[a-zA-Z])(?=.*[@#$%!]).{3,40})$", message = "Provide at least one letter and one number")
	private String password;

	@NotNull(message = "*Please Provide Mobile number")
	@Size(min = 10, max = 10)
	@Pattern(groups = Pattern.class, regexp = "(^$|[0-9]{10})", message = "provide valid Mobile Number")
	private String mobileNumber;

	private String role = "USER";

	private String picUrl;

	private boolean isActivated;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", email=" + email + ", password=" + password
				+ ", mobileNumber=" + mobileNumber + ", isActivated=" + isActivated + ", role=" + role + ", picUrl="
				+ picUrl + "]";
	}
}
