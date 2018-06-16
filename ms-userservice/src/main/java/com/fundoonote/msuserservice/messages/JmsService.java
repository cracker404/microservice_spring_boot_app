package com.fundoonote.msuserservice.messages;

import com.fundoonote.msuserservice.exception.UserException;
import com.fundoonote.msuserservice.models.OperationType;

public interface JmsService
{
   <T> void addToQueue(T object, OperationType ot, Object id) throws UserException;
}
