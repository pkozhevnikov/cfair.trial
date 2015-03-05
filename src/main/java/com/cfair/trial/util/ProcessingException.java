package com.cfair.trial.util;

import javax.ejb.ApplicationException;

@ApplicationException
public class ProcessingException extends Exception {

	private static final long serialVersionUID = 3200772322541022893L;
	
	public ProcessingException(String message, Throwable ex) {
		super(message, ex);
	}
	
}
