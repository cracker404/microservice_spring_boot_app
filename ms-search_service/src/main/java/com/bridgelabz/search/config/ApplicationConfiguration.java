package com.bridgelabz.search.config;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ApplicationConfiguration
{
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
}
