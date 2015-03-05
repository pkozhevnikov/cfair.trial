package com.cfair.trial;

import static com.cfair.trial.TestUtil.*;
import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cfair.trial.model.CurrencyPair;
import com.cfair.trial.model.Rate;
import com.cfair.trial.model.TradeMessage;
import com.cfair.trial.service.TradeMessageProcessor;
import com.cfair.trial.util.TradeMessageValidator;

public class ProcessorTest {
	
	private TradeMessageProcessor processor;
	
	@BeforeClass
	public void setup() {
		processor = new TradeMessageProcessor();
		processor.setMessageValidator(new TradeMessageValidator());
		
	}

	@Test(enabled=true)
	public void invalidMessagesSkipped() {

		TradeMessage m = makeMessage();
		m.setAmountBuy(null);
		processor.put(m);

		m = makeMessage();
		m.setAmountBuy(BigDecimal.ONE.negate());
		processor.put(m);
		
		m = makeMessage();
		m.setAmountSell(null);
		processor.put(m);
		
		m = makeMessage();
		m.setAmountSell(BigDecimal.ONE.negate());
		processor.put(m);
		
		m = makeMessage();
		m.setUserId(0);
		processor.put(m);
		
		m = makeMessage();
		m.setRate(null);
		processor.put(m);
		
		m = makeMessage();
		m.setRate(BigDecimal.valueOf(.3));
		processor.put(m);
		
		m = makeMessage();
		m.setRate(BigDecimal.valueOf(2.3));
		processor.put(m);

		final TradeMessage valid = makeMessage();
		processor.put(valid);
		
		Event<Collection<Rate>> events = new Events<Collection<Rate>>() {
			@Override
			public void fire(Collection<Rate> rates) {
				assertEquals(rates.size(), 1);
				Rate rate = rates.iterator().next();
				assertEquals(rate.getClose(), valid.getRate());
				assertEquals(rate.getCurrencyPair(), new CurrencyPair(valid.getCurrencyFrom(), valid.getCurrencyTo()));
				assertEquals(rate.getMax(), valid.getRate());
				assertEquals(rate.getMin(), valid.getRate());
				assertEquals(rate.getOpen(), BigDecimal.valueOf(.67));
				
			}
		};
		processor.setRateEvents(events);
		processor.process();
	}
	
	@Test
	public void processedOK() {
		for (int i = 1; i <= 4; i++)
			putMessage("EUR", "USD", "1." + i, "5." + i, "0");
		for (int i = 6; i <= 9; i++)
			putMessage("EUR", "USD", "0." + i, "0", "2." + i);
		for (int i = 1; i <= 3; i++)
			putMessage("GBP", "EUR", "1." + i, "3." + i, "3." + i);
		
		Event<Collection<Rate>> events = new Events<Collection<Rate>>() {
			@Override
			public void fire(Collection<Rate> rates) {
				assertEquals(rates.size(), 2);
				Map<CurrencyPair, Rate> rmap = new HashMap<>();
				for (Rate r : rates) rmap.put(r.getCurrencyPair(), r);
				
				CurrencyPair pair = new CurrencyPair(Currency.getInstance("EUR"), Currency.getInstance("USD"));
				Rate rate = rmap.get(pair);
				assertNotNull(rate);
				assertEquals(rate.getCurrencyPair(), pair);
				assertEquals(rate.getClose(), BigDecimal.valueOf(.9));
				assertEquals(rate.getMax(), BigDecimal.valueOf(1.4));
				assertEquals(rate.getMin(), BigDecimal.valueOf(.6));
				assertEquals(rate.getOpen(), BigDecimal.valueOf(1.1));
				assertEquals(rate.getTotalMessages(), 8);
				assertEquals(rate.getTotalBuy(), new BigDecimal("21.0"));
				assertEquals(rate.getTotalSell(), new BigDecimal("11.0"));
				
				pair = new CurrencyPair(Currency.getInstance("GBP"), Currency.getInstance("EUR"));
				rate = rmap.get(pair);
				assertNotNull(rate);
				assertEquals(rate.getCurrencyPair(), pair);
				assertEquals(rate.getClose(), BigDecimal.valueOf(1.3));
				assertEquals(rate.getMax(), BigDecimal.valueOf(1.3));
				assertEquals(rate.getMin(), BigDecimal.valueOf(1.1));
				assertEquals(rate.getOpen(), BigDecimal.valueOf(1.1));
				assertEquals(rate.getTotalMessages(), 3);
				assertEquals(rate.getTotalBuy(), new BigDecimal("9.6"));
				assertEquals(rate.getTotalSell(), new BigDecimal("9.6"));
			}
		};
		
		processor.setRateEvents(events);
		processor.process();
	}
	
	private void putMessage(String curfrom, String curto, String rateVal, String buyVal, String sellVal) {
		TradeMessage m = makeMessage();
		m.setCurrencyFrom(Currency.getInstance(curfrom));
		m.setCurrencyTo(Currency.getInstance(curto));
		m.setRate(new BigDecimal(rateVal));
		m.setAmountBuy(new BigDecimal(buyVal));
		m.setAmountSell(new BigDecimal(sellVal));
		processor.put(m);
	}

}
