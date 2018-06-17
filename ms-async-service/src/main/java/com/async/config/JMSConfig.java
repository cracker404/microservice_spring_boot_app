package com.async.config;

import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import com.async.service.MessageConsumer;

@Configuration
// @ConditionalOnExpression("'${mode}'.equals('development')")
public class JMSConfig 
{
	@Value("${activemq.url}")
	private String url;

	@Value("${activemq.queue}")
	private String queue;

	@Bean
	public CachingConnectionFactory cachingConnectionFactory() 
	{
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
		factory.setTrustAllPackages(true);
		return new CachingConnectionFactory(factory);
	}
	
	@Bean
	public <T> MessageListener msgListener() 
	{
		return new MessageConsumer<T>();
	}

	@Bean
	public DefaultMessageListenerContainer consumer() 
	{
		DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
		MessageListenerAdapter adapter = new MessageListenerAdapter();
		adapter.setDelegate(msgListener());
		dmlc.setMessageListener(adapter);
		dmlc.setDestinationName(queue);
		dmlc.setConnectionFactory(cachingConnectionFactory());
		return dmlc;
	}
}
