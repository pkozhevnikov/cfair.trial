package com.cfair.trial;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

public abstract class Events<T> implements Event<T> {

	@Override
	public Event<T> select(Annotation... qualifiers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U extends T> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <U extends T> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
		throw new UnsupportedOperationException();
	}

}
