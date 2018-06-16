package com.fundoonote.msuserservice.services;

import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.models.UserDTO;

public interface UserService 
{

	void save(User userl)throws UserException, Exception;

	void activation(String token)throws UserException;

	void uploadProfile(int loggedInUserId, MultipartFile file)throws UserException;

	void forgetPassword(String email)throws UserException, Exception;

	String resetPassword(String token)throws UserException;

	void changePassword(String token, String newPassword)throws UserException;

	UserDTO getProfile(Integer loggedInUserId)throws UserException;

}
