package com.async.service;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.async.model.Email;
import com.async.model.JmsDto;
import com.async.repository.ClientService;

public class MessageConsumer<T> implements MessageListener
{
	private final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	@Autowired
	private ClientService clientService;
	
	public MessageConsumer() {	}

	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(Message msg) {
		logger.info("Inside onMessage - fetching Object from activeMQ");

		try {
			ObjectMessage objectMessage = (ObjectMessage) msg;
			JmsDto<T> jmsDto = (JmsDto<T>) objectMessage.getObject();

			switch (jmsDto.getOperation()) {
			case SAVE:
				clientService.save(jmsDto.getObject(), jmsDto.getIndex(), (String)jmsDto.getId());
				System.out.println(clientService.send());
				break;
			case UPDATE:
				//clientService.update(jmsDto.getObject());
				break;
			case DELETE:
				//clientService.deleteById((int)jmsDto.getId());
				break;
			default:
				Email email = (Email) jmsDto.getObject();
				//clientService.send(email);
				break;
			}
		} catch (Exception e) {
			/*try {
				e.printStackTrace();
				ObjectMessage objectMessage = (ObjectMessage) msg;
				JmsDto<T> jmsDto = (JmsDto<T>) objectMessage.getObject();
				elasticSyncService.add(jmsDto);
			} catch (IllegalArgumentException | IllegalAccessException | JMSException | FNException e1) {
			}*/
		}
		logger.info("fetched and called ElasticService");
	}

}
