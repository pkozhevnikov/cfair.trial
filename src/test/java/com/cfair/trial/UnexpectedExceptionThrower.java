package com.cfair.trial;

import java.math.BigDecimal;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import com.cfair.trial.model.TradeMessage;
import com.cfair.trial.service.Gate;
import com.cfair.trial.util.ProcessingException;

public class UnexpectedExceptionThrower {
	
	@AroundInvoke
	public Object checkAndThrow(InvocationContext ic) throws Exception {
		if (Gate.class.isInstance(ic.getTarget()) &&
				ic.getMethod().getName().equals("add")) {
			TradeMessage message = (TradeMessage) ic.getParameters()[0];
			if (message.getAmountBuy().equals(BigDecimal.TEN))
				message.getAmountBuy().divide(BigDecimal.ZERO);
			if ("TZ".equals(message.getOriginatingCountry()))
				try {
					Integer.parseInt(message.getOriginatingCountry());
				} catch (Exception ex) {
					throw new ProcessingException("Cannot process origin country: TZ", ex);
				}
		}
		return ic.proceed();
	}

}
