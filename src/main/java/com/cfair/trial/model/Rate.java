package com.cfair.trial.model;

import java.math.BigDecimal;

public class Rate {

	private CurrencyPair currencyPair;
	private BigDecimal open;
	private BigDecimal min;
	private BigDecimal max;
	private BigDecimal close;
	private long period;
	private int totalMessages;
	private BigDecimal totalBuy = BigDecimal.ZERO;
	private BigDecimal totalSell = BigDecimal.ZERO;
	
	public CurrencyPair getCurrencyPair() {
		return currencyPair;
	}

	public void setCurrencyPair(CurrencyPair currencyPair) {
		this.currencyPair = currencyPair;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}
	
	public int getTotalMessages() {
		return totalMessages;
	}

	public void setTotalMessages(int totalMessages) {
		this.totalMessages = totalMessages;
	}

	public BigDecimal getTotalBuy() {
		return totalBuy;
	}

	public void setTotalBuy(BigDecimal totalBuy) {
		this.totalBuy = totalBuy;
	}

	public BigDecimal getTotalSell() {
		return totalSell;
	}

	public void setTotalSell(BigDecimal totalSell) {
		this.totalSell = totalSell;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currencyPair == null) ? 0 : currencyPair.hashCode());
		result = prime * result + (int) (period ^ (period >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rate other = (Rate) obj;
		if (currencyPair == null) {
			if (other.currencyPair != null)
				return false;
		} else if (!currencyPair.equals(other.currencyPair))
			return false;
		if (period != other.period)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rate [currencyPair=").append(currencyPair)
				.append(", period=").append(period).append(", open=")
				.append(open).append(", min=").append(min).append(", max=")
				.append(max).append(", close=").append(close)
				.append(", totalMessages=").append(totalMessages)
				.append(", totalBuy=").append(totalBuy).append(", totalSell=")
				.append(totalSell).append("]");
		return builder.toString();
	}

}
