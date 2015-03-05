package com.cfair.trial.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TradeMessage {

	private transient UUID uuid = UUID.randomUUID();
	private long userId;
	private Currency currencyFrom;
	private Currency currencyTo;
	private BigDecimal amountSell;
	private BigDecimal amountBuy;
	private BigDecimal rate;
	private Date timePlaced;
	private String originatingCountry;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Currency getCurrencyFrom() {
		return currencyFrom;
	}

	public void setCurrencyFrom(Currency currencyFrom) {
		this.currencyFrom = currencyFrom;
	}

	public Currency getCurrencyTo() {
		return currencyTo;
	}

	public void setCurrencyTo(Currency currencyTo) {
		this.currencyTo = currencyTo;
	}

	public BigDecimal getAmountSell() {
		return amountSell;
	}

	public void setAmountSell(BigDecimal amountSell) {
		this.amountSell = amountSell;
	}

	public BigDecimal getAmountBuy() {
		return amountBuy;
	}

	public void setAmountBuy(BigDecimal amountBuy) {
		this.amountBuy = amountBuy;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Date getTimePlaced() {
		return timePlaced;
	}

	public void setTimePlaced(Date timePlaced) {
		this.timePlaced = timePlaced;
	}

	public String getOriginatingCountry() {
		return originatingCountry;
	}

	public void setOriginatingCountry(String originatingCountry) {
		this.originatingCountry = originatingCountry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currencyFrom == null) ? 0 : currencyFrom.hashCode());
		result = prime * result
				+ ((currencyTo == null) ? 0 : currencyTo.hashCode());
		result = prime * result
				+ ((timePlaced == null) ? 0 : timePlaced.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		TradeMessage other = (TradeMessage) obj;
		if (currencyFrom == null) {
			if (other.currencyFrom != null)
				return false;
		} else if (!currencyFrom.equals(other.currencyFrom))
			return false;
		if (currencyTo == null) {
			if (other.currencyTo != null)
				return false;
		} else if (!currencyTo.equals(other.currencyTo))
			return false;
		if (timePlaced == null) {
			if (other.timePlaced != null)
				return false;
		} else if (!timePlaced.equals(other.timePlaced))
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@JsonIgnore
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeMessage [uuid=").append(uuid).append(", userId=");
		builder.append(userId);
		builder.append(", currencyFrom=");
		builder.append(currencyFrom);
		builder.append(", currencyTo=");
		builder.append(currencyTo);
		builder.append(", amountSell=");
		builder.append(amountSell);
		builder.append(", amountBuy=");
		builder.append(amountBuy);
		builder.append(", rate=");
		builder.append(rate);
		builder.append(", timePlaced=");
		builder.append(timePlaced);
		builder.append(", originatingCountry=");
		builder.append(originatingCountry);
		builder.append("]");
		return builder.toString();
	}
	
}
