package com.cfair.trial.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParseException;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
	
	@Override
	public Response toResponse(JsonParseException throwable) {
		ExceptionResponse er = new ExceptionResponse();
		er.setMessage(throwable.getMessage());
		er.setExceptionClass(throwable.getClass().getName());
		return Response.status(422).entity(er).build();
	}

}
