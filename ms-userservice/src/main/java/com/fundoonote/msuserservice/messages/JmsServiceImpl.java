package com.fundoonote.msuserservice.messages;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

   @SuppressWarnings("unchecked")
   @Override
   public <T> void addToQueue(T object, OperationType ot, Object id) throws UserException
   {
      logger.info("inside Jms Service for add obj to queue");
     
     /* Map<String, Object> map = new LinkedHashMap<>();
      map.put("Object", json);
      map.put("operationType", ot.toString());
      map.put("index", object.getClass().getSimpleName().toLowerCase());
      map.put("id", id);*/
      /*JmsDto<T> dto = new JmsDto<>();
      dto.setId(id);
      dto.setIndex(object.getClass().getSimpleName().toLowerCase());
      dto.setObject(object);
      dto.setOperation(ot);
      dto.setClazz((Class<T>) object.getClass());*/

      try {
    	  String json = mapper.writeValueAsString(object);
         jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException
            {
               MapMessage map = session.createMapMessage();
               map.setObject("object", json);
               map.setString("operationType", ot.toString());
               map.setString("index", object.getClass().getSimpleName().toLowerCase());
               map.setString("id", id.toString());
               return map;
            }
         });
      } catch (Exception e) {
        e.printStackTrace();
      }
   }

}
