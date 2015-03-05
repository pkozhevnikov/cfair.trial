package com.cfair.trial.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadArgumentsExceptionMapper implements ExceptionMapper<BadArgumentsException> {

	@Override
	public Response toResponse(BadArgumentsException exception) {
		
		ExceptionResponse er = new ExceptionResponse();
		er.setExceptionClass(exception.getClass().getName());
		er.setMessage(exception.getMessage());
		
		return Response.status(422).entity(er).build();
	}

}
