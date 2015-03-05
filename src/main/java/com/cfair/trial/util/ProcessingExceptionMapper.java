package com.cfair.trial.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {
	
	@Override
	public Response toResponse(ProcessingException throwable) {
		ExceptionResponse er = new ExceptionResponse();
		er.setMessage(throwable.getMessage());
		er.setExceptionClass(throwable.getCause().getClass().getName());
		er.setCauseMessage(throwable.getCause().getMessage());
		er.setStackTrace(throwable.getCause().getStackTrace());
		return Response.serverError().entity(er).build();
	}

}
