package com.cfair.trial.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {
	
	@Override
	public Response toResponse(Throwable throwable) {
		ExceptionResponse er = new ExceptionResponse();
		er.setMessage(throwable.getMessage());
		er.setStackTrace(throwable.getStackTrace());
		er.setExceptionClass(throwable.getClass().getName());
		return Response.serverError().entity(er).build();
	}

}
