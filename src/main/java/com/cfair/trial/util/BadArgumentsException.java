package com.cfair.trial.util;

import javax.ejb.ApplicationException;

@ApplicationException
public class BadArgumentsException extends IllegalArgumentException {

	private static final long serialVersionUID = 6776868088805200889L;
	
	public BadArgumentsException(String message) {
		super(message);
	}

}
