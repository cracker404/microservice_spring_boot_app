package com.fundoonote.msuserservice.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonote.msuserservice.config.ApplicationConfiguration;
import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.User;
import com.fundoonote.msuserservice.models.UserDTO;
import com.fundoonote.msuserservice.response.Response;
import com.fundoonote.msuserservice.response.UserFieldErrors;
import com.fundoonote.msuserservice.services.UserService;

/**
 * <p>
 * This is a Rest Controller for User With
 * {@link RestController @RestController}, we have added all general purpose
 * methods here those method will accept a rest request in JSON form and will
 * return a JSON response.
 * </p>
 * <p>
 * The methods are self explanatory we have used <b>{@code @RestController}</b>
 * annotation to point incoming requests to this class, and
 * <b>{@link ResponseBody @ResponseBody}</b> annotation to point incoming
 * requests to appropriate Methods. <b>{@link RequestBody @RequestBody}</b>
 * annotation is used to accept data with request in JSON form and Spring
 * ResponseEntity is used to return JSON as response to incoming request.
 * </p>
 * 
 * @version 1
 * @since 2017-03-10
 * @author Bridgelabz
 */
@RestController
public class UserController
{
   @Autowired
   private UserService userService;

   @Value("${redirect.login.url}")
   private String redirectUrl;
  

   @Value("${redirect.reset.url}")
   private String redirectResetPassURL;

   private final Logger logger = LoggerFactory.getLogger(UserController.class);

   /**
    * <p>
    * This rest API for new user registration/save With
    * {@link RequestMapping @RequestMapping} to mapped rest address.
    * </p>
    * 
    * @param user Object to be save, should not be null.
    * @return ResponseEntity with HTTP status and message.
    */
   @PostMapping("/save")
   public ResponseEntity<Response> save(@Valid @RequestBody User user, BindingResult result)
   {
      logger.debug("User Registration");
      Response response = null;
      if (result.hasErrors()) {
         logger.error("User Registration validation error %s", result.getFieldErrors());
         response = new UserFieldErrors();
         response.setStatus(112);
         response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("112"));
         ((UserFieldErrors) response).setErrors(result.getFieldErrors());
         return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
      }
      try {
         userService.save(user);
      } catch (UserException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         e.printStackTrace();
         UserException fn = new UserException(101, new Object[] { "User Registration - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response = new Response();
      response.setStatus(200);
      response.setResponseMessage("Registration successfull");
      logger.debug("Registration successfull");
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   /**
    * <p>
    * This rest API for user activation through email, get token from link. link
    * send to user valid email at the time of registration.
    * </p>
    * 
    * @param token JWT
    * @param response HTTP
    * @param request HTTP
    * @return redirect to login URL
    * @throws IOException if failed to redirection to login URL
    */
   @GetMapping(value = "/activate/{token:.+}")
   public ResponseEntity<Response> active(@PathVariable("token") String token, HttpServletResponse res)
   {
      logger.debug("User Activate", token);
      try 
      {
         userService.activation(token);
         logger.info("redirected to login page");
         res.sendRedirect(redirectUrl);
         return null;
      } 
      catch (UserException e) 
      {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) 
      {
         logger.error(e.getMessage());
         e.printStackTrace();
         UserException fn = new UserException(101, new Object[] { "user activate - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }
   @PostMapping("image")
   public ResponseEntity<?> saveImage(@RequestHeader(name="userId") Integer loggedInUserId , @RequestPart MultipartFile file)
   {
      Response response = new Response();
      try 
      {
         userService.uploadProfile(loggedInUserId, file);
      } catch (UserException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<Response>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         UserException fn = new UserException(101, new Object[] { "user login - " + e.getMessage() }, e);
         return new ResponseEntity<Response>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage("Image uploaded successfull");
      logger.debug("Image uploaded successfull", response);
      return new ResponseEntity<Response>(response, HttpStatus.OK);
   }
   @PostMapping("/forgotpassword")
   public ResponseEntity<Response> forgetPassword(@RequestParam String email)
   {
      Response response = new Response();
      try {
         userService.forgetPassword(email);
      } catch (UserException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         UserException fn = new UserException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
      logger.debug("Update successfull", response);
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @GetMapping("/resetpassword/{token:.+}")
   public ResponseEntity<Response> resetPassword(@PathVariable("token") String token, HttpServletResponse res)
   {
      try 
      {
         String resetToken = userService.resetPassword(token);
         redirectResetPassURL = redirectResetPassURL + "?resetToken=" + resetToken;
         res.sendRedirect(redirectResetPassURL);
         return null;
      } 
      catch (UserException e) 
      {
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      catch (Exception e) 
      {
         logger.error(e.getMessage());
         UserException fn = new UserException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   @PostMapping("changepassword/{token:.+}")
   public ResponseEntity<Response> changePassword(@PathVariable("token") String token, 
		   @RequestParam("newPassword")String newPassword)
   {
      try {
         userService.changePassword(token, newPassword);
      } 
      catch (UserException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         logger.error(e.getMessage());
         UserException fn = new UserException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
      logger.debug("Update successfull", response);
      return new ResponseEntity<>(response, HttpStatus.OK);
   }
/*
   @GetMapping("/google")
   public Principal user(Principal principal)
   {
      return principal;
   }
*/
   @GetMapping("profile")
   public ResponseEntity<?> getProfile(@RequestHeader(name="userId") Integer loggedInUserId)
   {
      logger.debug("Getting User profile");
      Response response = new Response();
      UserDTO user = null;
      try 
      {
         user = userService.getProfile(loggedInUserId);
      } 
      catch (UserException e) 
      {
         e.printStackTrace();
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) 
      {
         e.printStackTrace();
         logger.error(e.getMessage());
         UserException fn = new UserException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
      logger.debug("fetched User profile successfully %s", response);
      return new ResponseEntity<UserDTO>(user, HttpStatus.OK);
   }
   @GetMapping("/test")
   public String test(@RequestParam("word") String word) {
	   
	return word;
	   
   }
}
