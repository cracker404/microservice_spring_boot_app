package com.fundoonote.msuserservice.messages;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.OperationType;

@Service
public class JmsServiceImpl implements JmsService
{
   @Autowired
   private JmsTemplate jmsTemplate;

   private final Logger logger = LoggerFactory.getLogger(JmsServiceImpl.class);

   @SuppressWarnings("unchecked")
   @Override
   public <T> void addToQueue(T object, OperationType ot, Object id) throws UserException
   {
      logger.info("inside Jms Service for add obj to queue");
      JmsDto<T> dto = new JmsDto<>();
      dto.setId(id);
      dto.setIndex(object.getClass().getSimpleName().toLowerCase());
      dto.setObject(object);
      dto.setOperation(ot);
      dto.setClazz((Class<T>) object.getClass());

      try {
         jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException
            {
               return session.createObjectMessage(dto);
            }
         });
      } catch (Exception e) {
        
      }
   }

}
