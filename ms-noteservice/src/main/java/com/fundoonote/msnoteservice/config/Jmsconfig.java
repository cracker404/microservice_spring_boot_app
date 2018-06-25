package com.fundoonote.msnoteservice.config;

import javax.jms.Queue;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@EnableJms
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
		return new ActiveMQQueue("queue");
	}

	@Bean
	public ActiveMQConnectionFactory connectionFactory() 
	{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		connectionFactory.setPassword(brokerUserName);
		connectionFactory.setUserName(brokerPassword);
		connectionFactory.setTrustAllPackages(true);
		return connectionFactory;
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