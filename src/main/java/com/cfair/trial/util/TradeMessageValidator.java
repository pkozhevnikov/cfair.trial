package com.cfair.trial.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.cfair.trial.model.TradeMessage;

@ApplicationScoped
public class TradeMessageValidator {
	
	@AroundInvoke
	public Object validate(InvocationContext ic) throws Exception {
		for (Object param : ic.getParameters()) {
			if (!TradeMessage.class.isInstance(param)) continue;
			validate((TradeMessage) param);
		}
		return ic.proceed();
	}
	
	private static final BigDecimal MIN_RATE = BigDecimal.valueOf(0.5);
	private static final BigDecimal MAX_RATE = BigDecimal.valueOf(1.5);
	
	public void validate(TradeMessage m) throws BadArgumentsException {
		if (badValue(m.getAmountBuy()))
			throw new BadArgumentsException("Wrong buy amount: " + m.getAmountBuy());
		if (badValue(m.getAmountSell()))
			throw new BadArgumentsException("Wrong sell amount: " + m.getAmountSell());
		checkForNull(m.getCurrencyFrom(), "currency from");
		checkForNull(m.getCurrencyTo(), "currency to");
		if (m.getCurrencyFrom().equals(m.getCurrencyTo()))
			throw new BadArgumentsException("Cannot exchange to the same currency " +
					m.getCurrencyFrom() + "->" + m.getCurrencyFrom());
		checkForNull(m.getTimePlaced(), "time placed");
		checkForNull(m.getOriginatingCountry(), "originating country");
		if (!Arrays.asList(Locale.getISOCountries()).contains(m.getOriginatingCountry()))
			throw new BadArgumentsException("Inconsistent value of [originatingCountry]");
		if (m.getUserId() <= 0) throw new BadArgumentsException("Wrong user ID: " + m.getUserId());
		if (badValue(m.getRate()))
			throw new BadArgumentsException("Wrong rate: " + m.getRate());
		if (MIN_RATE.compareTo(m.getRate()) > 0)
			throw new BadArgumentsException(String.format("Wrong rate: %s. Minimum rate is %s",
					m.getRate(), MIN_RATE));
		if (MAX_RATE.compareTo(m.getRate()) < 0)
			throw new BadArgumentsException(String.format("Wrong rate: %s. Maximum rate is %s",
					m.getRate(), MAX_RATE));
	}
	
	public void checkForNull(Object value, String fieldName) {
		if (value == null) throw new BadArgumentsException("Wrong " + fieldName + ": null");
	}
	
	private boolean badValue(BigDecimal d) {
		return d == null || BigDecimal.ZERO.compareTo(d) > 0;
	}
	
}
