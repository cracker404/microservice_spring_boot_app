package com.fundoonote.msuserservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
//@ConditionalOnExpression("'${mode}'.equals('development')")
public class Jmsconfig
{
   @Value("${activemq.url}")
   private String url;

   @Value("${async.queue}")
   private String queue;
   
   @Bean
   public CachingConnectionFactory cachingConnectionFactory()
   {
      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
      factory.setTrustAllPackages(true);
      return new CachingConnectionFactory(factory);
   }

   @Bean
   public Queue queue()
   {
      return new ActiveMQQueue(queue);
   }

   @Bean
   public JmsTemplate jmsTemplate()
   {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(cachingConnectionFactory());
      template.setDefaultDestination(queue());
      return template;
   }

}
