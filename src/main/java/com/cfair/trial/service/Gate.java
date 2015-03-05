package com.cfair.trial.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cfair.trial.model.TradeMessage;
import com.cfair.trial.util.BadArgumentsException;
import com.cfair.trial.util.ProcessingException;
import com.cfair.trial.util.TradeMessageValidator;

@Stateless
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Gate {
	
	private static final Logger log = LoggerFactory.getLogger(Gate.class);
	
	@EJB
	private TradeMessageProcessor processor;

	@Interceptors(TradeMessageValidator.class)
	@POST
	@Path("add")
	public void add(TradeMessage message) throws ProcessingException {
		Future<TradeMessage> fut = processor.add(message);
		try { 
			fut.get();
			log.info("added {} syncly", message.getUuid());
		} catch (InterruptedException | ExecutionException ex) {
			throw new ProcessingException("Unknown processing status of " + message, ex);
		}
	}
	
	@POST
	@Path("addasync")
	public void addAsync(TradeMessage message) {
		processor.add(message);
		log.info("added {} asyncly", message.getUuid());
	}
	
	@POST
	public void put(TradeMessage message) {
		processor.put(message);
	}
	
	@GET
	@Path("list")
	public List<TradeMessage> getList() {
		List<TradeMessage> list = processor.getList();
		if (list.isEmpty()) 
			throw new BadArgumentsException("No records found");

		return list;
	}
	
	public void clear() {
		processor.clear();
	}

}
