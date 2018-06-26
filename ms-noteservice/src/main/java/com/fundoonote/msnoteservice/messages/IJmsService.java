package com.fundoonote.msnoteservice.messages;

import com.fundoonote.msnoteservice.exception.*;

public interface IJmsService
{
   <T> void addToQueue(T object, OperationType ot) throws NSException;
}
