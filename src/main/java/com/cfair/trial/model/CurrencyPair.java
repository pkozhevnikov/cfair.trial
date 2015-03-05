package com.cfair.trial.model;

import java.util.Currency;

public class CurrencyPair {

	private Currency from;
	private Currency to;
	
	public CurrencyPair(Currency from, Currency to) {
		this.from = from;
		this.to = to;
	}
	
	public CurrencyPair(String from, String to) {
		this.from = Currency.getInstance(from);
		this.to = Currency.getInstance(to);
	}

	public Currency getFrom() {
		return from;
	}

	public void setFrom(Currency from) {
		this.from = from;
	}

	public Currency getTo() {
		return to;
	}

	public void setTo(Currency to) {
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		CurrencyPair other = (CurrencyPair) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CurrencyPair [from=").append(from).append(", to=")
				.append(to).append("]");
		return builder.toString();
	}
	
}
