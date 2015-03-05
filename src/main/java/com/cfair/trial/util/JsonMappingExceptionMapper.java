package com.cfair.trial.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
	
	private static final Pattern PT_FIELD = Pattern
			.compile("through reference chain\\:[^\"]+\"([^\"]+)\"");
	
	@Override
	public Response toResponse(JsonMappingException throwable) {
		ExceptionResponse er = new ExceptionResponse();
		String message = throwable.getMessage();
		Matcher m = PT_FIELD.matcher(message);
		if (m.find())
			message = "Inconsistent value of [" + m.group(1) + "]";
		er.setMessage(message);
		er.setExceptionClass(throwable.getClass().getName());
		return Response.status(422).entity(er).build();
	}

}
