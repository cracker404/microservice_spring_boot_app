package com.fundoonote.msuserservice.validation;

import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.fundoonote.msuserservice.models.User;

public class CustomValidation implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		
		return User.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) 
	{
		ValidationUtils.rejectIfEmpty(errors, "name", "user.name.empty");
		ValidationUtils.rejectIfEmpty(errors, "email", "user.email.empty");
		ValidationUtils.rejectIfEmpty(errors, "password", "user.password.empty");
		ValidationUtils.rejectIfEmpty(errors, "mobileNumber", "user.mobileNumber.empty");

		User userDto = (User) target;

		Pattern pattern = Pattern.compile("^[a-zA-Z\\s]+", Pattern.CASE_INSENSITIVE);
		if (!(pattern.matcher(userDto.getName()).matches())) {
			errors.rejectValue("name", "user name invalid");
		}
		Pattern pattern1 = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$", Pattern.CASE_INSENSITIVE);
		if (!(pattern1.matcher(userDto.getEmail()).matches())) {
			errors.rejectValue("email", "user email invalid");
		}

		Pattern pattern2 = Pattern.compile("[A-Za-z0-9]{8,}");
		if (!(pattern2.matcher(userDto.getPassword()).matches())) {
			errors.rejectValue("password", "user password invalid");
		}
		Pattern pattern3 = Pattern.compile("^[0-9]{10}$");
		if (!(pattern3.matcher(userDto.getMobileNumber()).matches())) {
			errors.rejectValue("mobileNumber", "user mobileNumber invalid");
		}

	}

}
