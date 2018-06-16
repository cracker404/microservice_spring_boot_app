package com.fundoonote.msuserservice.exception;

import java.util.Locale;

import com.fundoonote.msuserservice.config.ApplicationConfiguration;
import com.fundoonote.msuserservice.response.ErrorResponse;
import com.fundoonote.msuserservice.response.Response;

public class UserException extends Exception
{
   private static final long serialVersionUID = 1L;

   protected long errorCode;
   protected Object[] args;

   public UserException(long errorCode)
   {
      this(errorCode, new Object[] {});
   }

   public UserException(long errorCode, Object[] args)
   {
      this(errorCode, args, null);
   }

   public UserException(long errorCode, Throwable pCause)
   {
      this(errorCode, new Object[] {}, pCause);
   }

   public UserException(long errorCode, Object[] args, Throwable pCause)
   {
      super(pCause);
      this.errorCode = errorCode;
      this.args = args;
   }

   public String getMessage()
   {
      return getDisplayMessage();
   }

   public String getDisplayMessage()
   {
      return getDisplayMessage(Locale.getDefault());
   }

   public String getDisplayMessage(Locale locale)
   {
      return getMessageInternal(false, locale);
   }

   private String getMessageInternal(boolean isLog, Locale plocale)
   {
      if (plocale == null) {
         plocale = Locale.getDefault();
      }

      String message = ApplicationConfiguration.getMessageAccessor().getMessage(errorCode + "", args, plocale);
      if (message == null || message.trim().isEmpty()) {
         UserException exp = new UserException(100);
         if (isLog) {
            // Fetal Error
            return exp.getLogMessage();
         }
         // Generic display message for end user
         return exp.getDisplayMessage(plocale);
      }

      String[] msgs = message.split("\\|");
      if (isLog) {
         return msgs[0];
      }

      if (msgs.length > 1 && !msgs[1].trim().isEmpty()) {
         return msgs[1].trim();
      }

      // Generic display message for end user
      UserException exp = new UserException(100);
      return exp.getDisplayMessage(plocale);
   }

   public String getLogMessage()
   {
      return getMessageInternal(true, null);
   }

   public Object[] getArgs()
   {
      return args;
   }

   public long getErrorCode()
   {
      return errorCode;
   }

   public Response getErrorResponse()
   {
      return getErrorResponse(Locale.getDefault());
   }

   public Response getErrorResponse(Locale locale)
   {
      Response err = new ErrorResponse();
      err.setStatus(getErrorCode());
      err.setResponseMessage(getDisplayMessage(locale));

      return err;
   }

}
