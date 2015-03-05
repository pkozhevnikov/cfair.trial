package com.cfair.trial.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cfair.trial.model.CurrencyPair;
import com.cfair.trial.model.Rate;
import com.cfair.trial.model.TradeMessage;
import com.cfair.trial.util.BadArgumentsException;
import com.cfair.trial.util.LockableConcurrentLinkedQueue;
import com.cfair.trial.util.TradeMessageValidator;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TradeMessageProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(TradeMessageProcessor.class);
	
	private List<TradeMessage> messages = Collections.synchronizedList(new ArrayList<TradeMessage>());
	
	private LockableConcurrentLinkedQueue<TradeMessage> queue = 
			new LockableConcurrentLinkedQueue<>();
			
	@Inject
	private TradeMessageValidator messageValidator;
	
	@Inject
	private Event<Collection<Rate>> rateEvents;

	@Asynchronous
	public Future<TradeMessage> add(TradeMessage message) {
		try {
			messageValidator.validate(message);
			return addValidated(message);
		} catch (BadArgumentsException ex) {
			log.warn("skipped invalid message {}", message);
			return new AsyncResult<TradeMessage>(null);
		}
	}
	
	@Asynchronous
	public Future<TradeMessage> addValidated(TradeMessage message) {
		messages.add(message);
		log.info("processed {}", message.getUuid());
		return new AsyncResult<TradeMessage>(message);
	}
	
	@Asynchronous
	public void put(TradeMessage message) {
		try {
			messageValidator.validate(message);
			queue.offer(message);
		} catch (BadArgumentsException ex) {
			log.warn("Skipped invalid message {}", message);
		}
	}
	
	@Schedule(second="*",hour="*",minute="*")
	public void process() {
		try {
			queue.lock();
			long period = System.currentTimeMillis();
			TradeMessage message;
			Map<CurrencyPair, Rate> rates = new HashMap<>();

			while ((message = queue.poll()) != null) {
				CurrencyPair pair = new CurrencyPair(message.getCurrencyFrom(), message.getCurrencyTo());
				Rate rate = rates.get(pair);
				if (rate == null) {
					rate = new Rate();
					rate.setCurrencyPair(pair);
					rate.setPeriod(period);
					rate.setMin(message.getRate());
					rate.setMax(message.getRate());
					rate.setOpen(message.getRate());
					rates.put(pair, rate);
				}
				rate.setMin(message.getRate().min(rate.getMin()));
				rate.setMax(message.getRate().max(rate.getMax()));
				rate.setClose(message.getRate());
				rate.setTotalMessages(rate.getTotalMessages() + 1);
				rate.setTotalBuy(rate.getTotalBuy().add(message.getAmountBuy()));
				rate.setTotalSell(rate.getTotalSell().add(message.getAmountSell()));
			}
			rateEvents.fire(rates.values());
		} catch (Exception ex) {
			log.error("Processing error", ex);
		} finally {
			queue.unlock();
		}
	}
	
	public List<TradeMessage> getList() {
		return Collections.unmodifiableList(messages);
	}

	public void clear() {
		messages.clear();
	}
	
	public void setMessageValidator(TradeMessageValidator messageValidator) {
		this.messageValidator = messageValidator;
	}
	
	public void setRateEvents(Event<Collection<Rate>> rateEvents) {
		this.rateEvents = rateEvents;
	}

}
