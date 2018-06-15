package com.fundoonote.msnoteservice.utility.messagesservice;

import com.fundoonote.msnoteservice.exception.*;
import com.fundoonote.msnoteservice.utility.OperationType;

public interface IJmsService
{
   <T> void addToQueue(T object, OperationType ot) throws NSException;
}
