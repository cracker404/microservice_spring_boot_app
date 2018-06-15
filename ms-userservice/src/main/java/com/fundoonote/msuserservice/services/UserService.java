package com.fundoonote.msuserservice.services;

import java.io.IOException;

import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.User;

public interface UserService 
{

	void save(User user, String url)throws UserException, IOException;

}
