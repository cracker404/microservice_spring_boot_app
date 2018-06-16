package com.fundoonote.msuserservice.config;

import java.util.Locale;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ApplicationConfiguration
{
   @Value("${aws.s3.access.key}")
   private String s3Access;

   @Value("${aws.s3.secret.key}")
   private String s3secretKey;

   @Value("${aws.s3.region}")
   private String region;
   
   @Bean
   public PasswordEncoder passwordEncoder()
   {
      return new BCryptPasswordEncoder();
   }

   private static MessageSourceAccessor messageSourceAccessor;

   @Bean
   public LocaleResolver localeResolver()
   {
      SessionLocaleResolver slr = new SessionLocaleResolver();
      slr.setDefaultLocale(Locale.US);
      return slr;
   }

   @PostConstruct
   private void initMessageSourceAccessor()
   {
      ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
      messageSource.setBasename("classpath:messages/errormessages");
      messageSourceAccessor = new MessageSourceAccessor(messageSource, Locale.getDefault());

   }

   public static MessageSourceAccessor getMessageAccessor()
   {
      return messageSourceAccessor;
   }

   @Bean
   public ObjectMapper getObjectMapper()
   {
      return new ObjectMapper();
   }
   @Bean
   public Executor asyncExecutor() {
       ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
       executor.setCorePoolSize(2);
       executor.setMaxPoolSize(2);
       executor.setQueueCapacity(500);
       executor.setThreadNamePrefix("UserService-");
       executor.initialize();
       return executor;
   }
   
   @Bean
   public AmazonS3 s3client() {
      BasicAWSCredentials awsCreds = new BasicAWSCredentials(s3Access, s3secretKey);
      return AmazonS3ClientBuilder.standard().withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
   }
   
}
