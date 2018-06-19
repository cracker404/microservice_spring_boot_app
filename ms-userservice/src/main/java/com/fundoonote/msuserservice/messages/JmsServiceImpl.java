package com.fundoonote.msuserservice.messages;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.OperationType;

@Service
public class JmsServiceImpl implements JmsService
{
   @Autowired
   private JmsTemplate jmsTemplate;
   
	@Autowired
	private ObjectMapper mapper;

   private final Logger logger = LoggerFactory.getLogger(JmsServiceImpl.class);

   @Override
   public <T> void addToQueue(T object, OperationType ot, Object id) throws UserException
   {
      logger.info("inside Jms Service for add obj to queue");
     
      HashMap<String, Object> map = new HashMap<>();
     
      try 
      {
    	  String json = mapper.writeValueAsString(object);
          map.put("Object", json);
          map.put("operationType", ot.toString());
          map.put("index", object.getClass().getSimpleName().toLowerCase());
          map.put("id", id);
          jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException
            {
            	return session.createObjectMessage(map);
            }
         });
      } catch (Exception e) {
        e.printStackTrace();
      }
   }

}
