package com.fundoonote.msuserservice.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msuserservice.chache.RedisService;
import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.mail.MailService;
import com.fundoonote.msuserservice.messages.JmsService;
import com.fundoonote.msuserservice.models.OperationType;
import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.models.UserDTO;
import com.fundoonote.msuserservice.repositories.UserDAO;

@Service
public class UserServiceImpl implements UserService {
	@Value("${email.reg.subject}")
	private String regSubject;

	@Value("${email.forget.subject}")
	private String forgetSub;

	@Value("${email.reg.path}")
	private String regFilePath;

	@Value("${email.forget.path}")
	private String forgetFilePath;

	@Value("${zuul.base.url}")
	private String baseUrl;

	@Autowired
	private MailService mailService;
	@Autowired
	private TokenHelper tokenHelper;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private JmsService jmsService;
	@Autowired
	private RedisService redisService;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void save(User user) throws UserException, Exception {
		User userFromDB = userDAO.findByEmail(user.getEmail());

		if (userFromDB == null) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userDAO.save(user);
			Map<String, Object> map = new HashMap<>();
			map.put("role", user.getRole());
			redisService.save("USER", "user" + user.getUserId(), map);
			String token = tokenHelper.generateToken(user);
			if (!baseUrl.endsWith("/"))
				baseUrl.concat("/");
			String link = baseUrl.concat("user/active/" + token);
			String body = getBodyFromFile(regFilePath);
			body = body.replace("$NAME$", user.getName());
			body = body.replace("$LINK$", link);
			mailService.send(regSubject, body, user.getEmail());
			/*
			 * jmsService.addToQueue(email, OperationType.MAIL, null);
			 * jmsService.addToQueue(user, OperationType.SAVE, user.getUserId());
			 */
			return;
		}
		throw new UserException(106);
	}

	private String getBodyFromFile(String filePath) throws IOException {
		File file = ResourceUtils.getFile(filePath);
		/*
		 * ClassLoader classLoader = getClass().getClassLoader(); File file = new
		 * File(classLoader.getResource(filePath).getFile());
		 */
		return new String(Files.readAllBytes(Paths.get(file.toURI())));
	}

	@Override
	public void activation(String token) throws UserException {
		Map<String, Object> map = tokenHelper.validateToken(token);
		int userId = (int) map.get("id");
		Optional<User> optional = userDAO.findById(userId);
		User user = optional.get();
		if (user == null) {
			throw new UserException(105);
		}
		user.setActivated(true);
		userDAO.save(user);

		jmsService.addToQueue(user, OperationType.UPDATE, user.getUserId());
	}

	@Override
	public void uploadProfile(int loggedInUserId, MultipartFile file) throws UserException {
		Optional<User> optional = userDAO.findById(loggedInUserId);
		User fromDB = optional.get();
		if (fromDB == null) {
			throw new UserException(105);
		}

		String imageUrl = s3Service.saveImageToS3(loggedInUserId + "-USER", file);

		fromDB.setPicUrl(imageUrl);
		userDAO.save(fromDB);

		jmsService.addToQueue(fromDB, OperationType.UPDATE, fromDB.getUserId());

	}

	@Override
	public void forgetPassword(String email) throws UserException, Exception 
	{
		User fromDB = userDAO.findByEmail(email);
		if (fromDB == null)
			throw new UserException(110);
		String token = tokenHelper.generateToken(fromDB);
		if (!baseUrl.endsWith("/"))
			baseUrl.concat("/");
		String link = baseUrl.concat("user/resetpassword/" + token);
		String body = getBodyFromFile(forgetFilePath);
		body = body.replace("$NAME$", fromDB.getName());
		body = body.replace("$EMAIL$", fromDB.getEmail());
		body = body.replace("$LINK$", link);

		mailService.send(regSubject, body, fromDB.getEmail());
	}

	@Override
	public String resetPassword(String token) throws UserException 
	{

		Map<String, Object> map = tokenHelper.validateToken(token);
		int userId = (int) map.get("id");

		Optional<User> optional = userDAO.findById(userId);
		User fromDB = optional.get();
		if (fromDB == null) {
			throw new UserException(105);
		}
		return tokenHelper.generateToken(fromDB);
	}

	@Override
	public void changePassword(String token, String newPassword) throws UserException
	{
		Map<String, Object> map = tokenHelper.validateToken(token);
		int userId = (int) map.get("id");

		Optional<User> optional = userDAO.findById(userId);
		User fromDB = optional.get();
		if (fromDB == null) {
			throw new UserException(105);
		}
		
		fromDB.setPassword(passwordEncoder.encode(newPassword));
		userDAO.save(fromDB);

		jmsService.addToQueue(fromDB, OperationType.UPDATE, fromDB.getUserId());
	}

	@Override
	public UserDTO getProfile(Integer loggedInUserId) throws UserException 
	{
		Optional<User> optional = userDAO.findById(loggedInUserId);
		User fromDB = optional.get();
		if (fromDB == null) {
			throw new UserException(105);
		}
		UserDTO dto = new UserDTO(fromDB.getUserId(), fromDB.getName(), fromDB.getEmail(), fromDB.getPicUrl(), fromDB.getRole());
		return dto;
	}

}
