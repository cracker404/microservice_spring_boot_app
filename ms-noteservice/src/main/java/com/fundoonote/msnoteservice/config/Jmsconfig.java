package com.fundoonote.msnoteservice.config;

import javax.jms.Queue;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * <p>
 * This is a configuration for Note Service application With
 * {@link Configuration @Configuration}, {@link EnableJMS @EnableJMS},
 *  we have created a bean of JMS Templates and 
 *  ActiveMqConnectionfactory
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
@ConditionalOnExpression("'${mode}'.equals('development')")
public class Jmsconfig 
{
	@Value("${broker.url}")
	private String url;

	@Value("${async.queue}")
	private String queue;

	@Value("${broker.username")
	private String brokerUserName;

	@Value("${broker.password")
	private String brokerPassword;

	@Bean
	public Queue queue() 
	{
		return new ActiveMQQueue(queue);
	}

	@Bean
	public CachingConnectionFactory connectionFactory() 
	{
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
		factory.setPassword(brokerUserName);
		factory.setUserName(brokerPassword);
		factory.setTrustAllPackages(true);
	    return new CachingConnectionFactory(factory);
	}

	@Bean
	public JmsTemplate jmsTemplate() 
	{
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setDefaultDestination(queue());// set destination of queue
		return template;
	}
}