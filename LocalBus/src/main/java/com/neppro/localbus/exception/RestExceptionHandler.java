package com.neppro.localbus.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.neppro.localbus.dto.Fault;
import com.neppro.localbus.dto.ValidationError;


@ControllerAdvice
public class RestExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
	
	@Autowired
	private MessageSource messageSource;
	private Map<String,Integer> errorCodeMap=new HashMap<String,Integer>();
	
	@PostConstruct
	private void initializeErrorCodeMap(){
		errorCodeMap.put("NotFoundException",404);
		errorCodeMap.put("AuthorizationException",401);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Fault> handleVehicleCategoryNotFoundException(NotFoundException exception) {
		Fault fault=new Fault(errorCodeMap.get(exception.getClass().getSimpleName()),exception.getMessage());
		logger.error(exception.getMessage());
		return new ResponseEntity<Fault>(fault, HttpStatus.NOT_FOUND);

	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }
 
    private ResponseEntity<ValidationError> processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError();
        for (FieldError fieldError: fieldErrors) {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            validationError.addFieldError(fieldError.getField(), localizedErrorMessage);
        }
        return new ResponseEntity<ValidationError>(validationError, HttpStatus.BAD_REQUEST);
    }
 
    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        System.out.println("Country Name: "+currentLocale.getCountry());
        String localizedErrorMessage = messageSource.getMessage(fieldError, currentLocale);
        //If the message was not found, return the most accurate field error code instead.
        //You can remove this check if you prefer to get the default error message.
        if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }
        return localizedErrorMessage;
    }
    
   	@ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Fault> processAuthorizationError(Exception exception) {
   		Fault fault=new Fault(errorCodeMap.get(exception.getClass().getSimpleName()),"NOT_AUTHORIZED");
   		return new ResponseEntity<Fault>(fault, HttpStatus.UNAUTHORIZED);
    }


}
