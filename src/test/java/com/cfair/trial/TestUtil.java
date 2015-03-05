package com.cfair.trial;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import com.cfair.trial.model.TradeMessage;

public class TestUtil {
	
	public static String buildTradeMessageJson(Map<String, Object> specVals) {
		Map<String, Object> vals = new HashMap<>();
		vals.put("userId", "134256");
		vals.put("currencyFrom", "EUR");
		vals.put("currencyTo", "GBP");
		vals.put("amountSell", 1000);
		vals.put("amountBuy", 747.10);
		vals.put("rate", 0.7471);
		vals.put("timePlaced", "14-JAN-15 10:27:44");
		vals.put("originatingCountry", "FR");
		
		vals.putAll(specVals);
		
		StringBuilder sb = new StringBuilder("{");
		for (Entry<String, Object> et : vals.entrySet()) {
			sb.append("\"").append(et.getKey()).append("\":");
			boolean isstring = String.class.isInstance(et.getValue());
			if (isstring) sb.append("\"");
			sb.append(et.getValue());
			if (isstring) sb.append("\"");
			sb.append(",");
		}
		sb.delete(sb.length() - 1, sb.length()).append("}");
		
		return sb.toString();
	}
	
	public static TradeMessage makeMessage() {
		TradeMessage m = new TradeMessage();
		m.setAmountBuy(BigDecimal.valueOf(1));
		m.setAmountSell(BigDecimal.valueOf(2.5));
		m.setUserId(1111);
		m.setCurrencyFrom(Currency.getInstance("RUR"));
		m.setCurrencyTo(Currency.getInstance("USD"));
		m.setRate(BigDecimal.valueOf(.67));
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.set(2015, 1, 28, 12, 59, 13);
		c.set(Calendar.MILLISECOND, 0);
		m.setTimePlaced(c.getTime());
		m.setOriginatingCountry("RU");
		return m;
	}
	

}
