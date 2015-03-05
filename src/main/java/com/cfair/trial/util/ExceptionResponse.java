package com.cfair.trial.util;

import java.io.Serializable;

public class ExceptionResponse implements Serializable {

	private static final long serialVersionUID = -3955012638778438714L;

	private String message;
	private String causeMessage;
	private String exceptionClass;
	private StackTraceElement[] stackTrace;
	
	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setCauseMessage(String causeMessage) {
		this.causeMessage = causeMessage;
	}
	
	public String getCauseMessage() {
		return causeMessage;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExceptionResponse [exceptionClass=")
				.append(exceptionClass).append(", message=").append(message)
				.append(", causeMessage=").append(causeMessage).append("]");
		return builder.toString();
	}

}
