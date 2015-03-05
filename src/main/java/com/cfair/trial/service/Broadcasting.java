package com.cfair.trial.service;

import java.util.Collection;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.atmosphere.config.service.ManagedService;
import org.atmosphere.cpr.BroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cfair.trial.model.Rate;
import com.cfair.trial.util.JacksonJsonProviderExt;

@ManagedService(path = "/live", atmosphereConfig = org.atmosphere.cpr.ApplicationConfig.MAX_INACTIVE
		+ "=120000")
public class Broadcasting {

	private static final Logger log = LoggerFactory
			.getLogger(Broadcasting.class);

	@Inject
	private JacksonJsonProviderExt jsonProvider;

	@SuppressWarnings("deprecation")
	public void ratesAppeared(@Observes Collection<Rate> rates) {
		BroadcasterFactory bcf = BroadcasterFactory.getDefault();
		if (bcf == null) return;
		try {
			bcf.lookup("/live").broadcast(jsonProvider.getMapper().writeValueAsString(rates));
		} catch (Exception ex) {
			log.error("Could not broadcast rates", ex);
		}
	}

}
