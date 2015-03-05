package com.cfair.trial.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
@ApplicationScoped
public class JacksonJsonProviderExt extends JacksonJsonProvider {

	public JacksonJsonProviderExt() {
		super();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,	false);
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		mapper.setDateFormat(sdf);
		setMapper(mapper);
	}
	
	public ObjectMapper getMapper() {
		return _mapperConfig.getConfiguredMapper(); 
	}
	
	public <T> T readEntity(InputStream inputStream, Class<T> clazz) throws IOException {
		return getMapper().readValue(inputStream, clazz);
	}

	public <T> List<T> readList(InputStream inputStream, Class<T> elementClazz) throws IOException {
		JavaType type = getMapper().getTypeFactory().constructCollectionType(List.class, elementClazz);
		return getMapper().readValue(inputStream, type);
	}
	
}
