package com.fundoonote.msuserservice.services;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.fundoonote.msuserservice.chache.RedisService;
import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.messages.JmsService;
import com.fundoonote.msuserservice.models.Email;
import com.fundoonote.msuserservice.models.OperationType;
import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.repositories.UserDAO;

@Service
public class UserServiceImpl  implements UserService
{
	@Value("${email.reg.subject}")
	private String regSubject;

	@Value("${email.forget.subject}")
	private String forgetSub;

	@Value("${email.reg.path}")
	private String regFilePath;
	
	@Value("${email.forget.path}")
	private String forgetFilePath;
	
	@Autowired
	private TokenHelper tokenHelper;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private JmsService jmsService;
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void save(User user, String link) throws UserException, IOException 
	{
		User userFromDB = userDAO.findByEmail(user.getEmail());

		if (userFromDB == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userDAO.save(user);
			Map<String, Object> map = new HashMap<>();
			map.put("role", user.getRole());
			redisService.save("USER", "user"+user.getUserId(), map);
			String token = tokenHelper.generateToken(user);

			link = link.substring(0, link.lastIndexOf('/')) + "/activate/" + URLEncoder.encode(token, "UTF-8");
			String body = getBodyFromFile(regFilePath);
			body = body.replace("$NAME$", user.getName());
			body = body.replace("$LINK$", link);
			Email email = new Email(user.getEmail(), body, regSubject);
			jmsService.addToQueue(email, OperationType.MAIL, null);
			jmsService.addToQueue(user, OperationType.SAVE, user.getUserId());
			return;
		}
		throw new UserException(106);
	}
	private String getBodyFromFile(String filePath) throws IOException
	{
		File file = ResourceUtils.getFile(filePath);
		/*
		 * ClassLoader classLoader = getClass().getClassLoader(); File file = new
		 * File(classLoader.getResource(filePath).getFile());
		 */
		return new String(Files.readAllBytes(Paths.get(file.toURI())));
	}

}
