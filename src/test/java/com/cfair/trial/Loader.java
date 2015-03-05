package com.cfair.trial;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;

import com.cfair.trial.model.CurrencyPair;
import com.cfair.trial.model.TradeMessage;
import com.cfair.trial.util.JacksonJsonProviderExt;

public class Loader implements Runnable {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: java -cp test-classes.jar com.cfair.trial.Loader URL THREADS_PER_PAIR [[CURRENCYFROM/CURRENCYTO] ...]");
			System.out.println("  example: java -cp test-classes.jar com.cfair.trial.Loader http://localhost:8080/cfair/service 4 EUR/USD RUR/GBP");
			System.out.println("  will post data for currency pairs EUR/USD and RUR/GBP, 4 threads for each pair. "
					+ "Default pair is USD/EUR");
			System.exit(0);
		}
		int tpp = 0;
		try {
			tpp = Integer.parseInt(args[1]);
			if (tpp < 1 || tpp > 10)
				throw new Exception();
		} catch (Exception ex) {
			System.err.println("Bad value for THREADS_PER_PAIR: " + args[1]);
			System.exit(1);
		}
		Set<CurrencyPair> pairs = new HashSet<>();
		if (args.length > 2)
			for (int i = 2; i < args.length; i++) {
				String[] ss = args[i].split("/");
				if (ss.length != 2) {
					System.err.println("Bad currency pair: " + args[i]);
					System.exit(1);
				}
				try {
					pairs.add(new CurrencyPair(ss[0], ss[1]));
				} catch (Exception ex) {
					System.err.println("Cannot create currency pair " + args[i]);
					System.exit(1);
				}
			}
		if (pairs.isEmpty())
			pairs.add(new CurrencyPair("USD", "EUR"));
		
		for (CurrencyPair pair : pairs) {
			for (int i = 0; i < tpp; i++)
				new Thread(new Loader(args[0], pair)).start();
			System.out.println("Started loader of " + pair);
		}
	}
	
	private WebClient client;
	private CurrencyPair pair;
	
	private Loader(String url, CurrencyPair pair) {
		this.client = WebClient.create(url, 
				Collections.singletonList(new JacksonJsonProviderExt()))
				.type(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				;
		this.pair = pair;
	}

	@Override
	public void run() {
		Random rand = new Random();
		int delay = 300;
		try {
			delay = Integer.parseInt(System.getProperty("loader.delay"));
		} catch (Exception ignore) {}
		while (true) {
			try {
				Thread.sleep(rand.nextInt(delay));
				TradeMessage m = TestUtil.makeMessage();
				m.setRate(BigDecimal.valueOf(rand.nextDouble() + .5));
				m.setAmountBuy(new BigDecimal(rand.nextInt(100) + "." + rand.nextInt(100)));
				m.setAmountSell(new BigDecimal(rand.nextInt(100) + "." + rand.nextInt(100)));
				m.setCurrencyFrom(pair.getFrom());
				m.setCurrencyTo(pair.getTo());
				client.post(m);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
	

}
