package com.fundoonote.msnoteservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fundoonote.msnoteservice.response.Response;

/**
 * <p>
 * This is a class for NoteService Exception which contains methods 
 * for handling the exceptions globally
 * </p>
 * 
 * @version 1
 * @since 2017-03-10
 * @author Bridgelabz
 */
@ControllerAdvice
public class NSExceptionHandler {

	Logger logger = LoggerFactory.getLogger(NSExceptionHandler.class);

	@ExceptionHandler(NSException.class)
	public ResponseEntity<Response> noteServiceExceptionHandler(NSException noteServiceException) {
		logger.info(noteServiceException.getMessage());
		return new ResponseEntity<Response>(noteServiceException.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> runTimeExceptionHandler(Exception exception) {
		NSException ex = new NSException(101, new Object[] {" "+ exception.getMessage() }, exception);
		logger.error(ex.getLogMessage());
		return new ResponseEntity<Response>(ex.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
