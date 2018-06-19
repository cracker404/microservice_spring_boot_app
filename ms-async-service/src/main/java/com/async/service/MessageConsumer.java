package com.async.service;

import java.util.HashMap;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
			HashMap<String, Object> map = (HashMap<String, Object>) objectMessage.getObject();
			String operation = (String) map.get("operationType");
			Object object = null;
			if(operation.equals("SAVE") || operation.equals("UPDATE")) {
				object = map.get("Object");
			}
			String index = (String) map.get("index");
			Integer id =  (Integer) map.get("id");
			switch (operation) {
			case "SAVE":
				clientService.save(object, index, String.valueOf(id));
				break;
			case "UPDATE":
				clientService.update(object, index,  String.valueOf(id));
				break;
			case "DELETE":
				clientService.deleteById(index,  String.valueOf(id));
				break;
			default:
				//Email email = (Email) jmsDto.getObject();
				//clientService.send(email);
				break;
			}
			/*switch (mapMessage.getString("operationType")) {
			case "SAVE":
				clientService.save(obj, mapMessage.getString("index"), mapMessage.getString("id"));
				break;
			case "UPDATE":
				clientService.update(obj, mapMessage.getString("index"), mapMessage.getString("id"));
				break;
			case "DELETE":
				clientService.deleteById(mapMessage.getString("index"), mapMessage.getString("id"));
				break;
			default:
				//Email email = (Email) jmsDto.getObject();
				//clientService.send(email);
				break;
			}*/
		} catch (Exception e) {
			e.printStackTrace();
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
