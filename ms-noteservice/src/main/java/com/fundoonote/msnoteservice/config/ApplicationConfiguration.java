package com.fundoonote.msnoteservice.config;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * This is a configuration for Note Service application With
 * {@link Configuration @Configuration}, we have created a bean 
 * of Amazon s3client and object mapper
 * </p>
 * <p>
 * The methods are invoked at the time of creating of bean
 * </p>
 * 
 * @version 1
 * @since 2017-03-10
 * @author Bridgelabz
 */
@Configuration
public class ApplicationConfiguration {

	@Value("${aws.s3.access.key}")
	private String s3Access;

	@Value("${aws.s3.secret.key}")
	private String s3secretKey;

	@Value("${aws.s3.region}")
	private String region;

	private static MessageSourceAccessor messageSourceAccessor;

	@Bean
	public AmazonS3 s3client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(s3Access, s3secretKey);
		return AmazonS3ClientBuilder.standard().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	}

	@PostConstruct
	private void initMessageSourceAccessor() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages/errormessage");
		messageSourceAccessor = new MessageSourceAccessor(messageSource, Locale.getDefault());

	}

	public static MessageSourceAccessor getMessageAccessor() {
		return messageSourceAccessor;
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
}
