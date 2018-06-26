package com.fundoonote.msnoteservice.messages;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fundoonote.msnoteservice.exception.NSException;


@Service
public class JmsServiceImpl implements IJmsService
{
   @Autowired
   private JmsTemplate jmsTemplate;

   private final Logger logger = LoggerFactory.getLogger(JmsServiceImpl.class);

   @SuppressWarnings("unchecked")
   public <T> void addToQueue(T object, OperationType ot) throws NSException
   {
      logger.info("inside Jms Service for add obj to queue");
      JmsDto<T> dto = new JmsDto<>();
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
      } catch (Exception e1) {
            e1.printStackTrace();
         }
      }
   }

