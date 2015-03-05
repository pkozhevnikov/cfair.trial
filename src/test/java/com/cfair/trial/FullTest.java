package com.cfair.trial;

import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cfair.trial.model.TradeMessage;
import com.cfair.trial.service.Gate;
import com.cfair.trial.util.BadArgumentsException;
import com.cfair.trial.util.ExceptionResponse;
import com.cfair.trial.util.JacksonJsonProviderExt;
import com.cfair.trial.util.ProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class FullTest extends Arquillian {
	
	private static final Logger log = LoggerFactory.getLogger(FullTest.class);
	
	@EJB
	private Gate service;
	
	@ArquillianResource
	private URL webappUrl;
	
	public FullTest() {
	}
	
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive arch = ShrinkWrap.create(WebArchive.class, "cfair.war")
				.addPackages(true, "com.cfair.trial")
				.addAsWebInfResource(new File("src/test/resources/ejb-jar.xml"))
				.addAsWebInfResource(new File("src/main/resources/META-INF/openejb-jar.xml"))
				.addAsWebInfResource(new File("src/main/resources/META-INF/beans.xml"))
				.merge(ShrinkWrap.create(GenericArchive.class)
						   .as(ExplodedImporter.class)
						   .importDirectory("src/main/webapp").as(GenericArchive.class),
						   "/", Filters.includeAll())
				
				.addAsLibraries(Maven.resolver().resolve(
						"org.atmosphere:atmosphere-runtime:2.2.5"
						).withTransitivity().asFile())
				
				;
		log.info("test archive: {}", arch.toString(true));
		return arch;
	}
	
	private HttpClient htclient;
	private JacksonJsonProviderExt jsonProvider;
	private WebClient client;
	
	@BeforeClass
	public void setup() throws MalformedURLException {
		htclient = HttpClientBuilder.create().build();
		jsonProvider = new JacksonJsonProviderExt();
	}
	
	@AfterClass
	public void teardown() throws InterruptedException {
		if (System.getProperty("__join__") != null) {
			log.warn("freeze");
			Thread.currentThread().join();
		}
	}
	
	private WebClient getClient() {
		if (client == null) {
			client = WebClient.create(webappUrl.toString(), Collections.singletonList(jsonProvider)) 
					.type(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.path("service")
					;
		}
		return client;
	}
	
	@Test(enabled=true,groups="ejbexc",
			expectedExceptions=BadArgumentsException.class,expectedExceptionsMessageRegExp="Wrong buy amount: null")
	public void wrongBuyAmount() throws ProcessingException {
		service.add(new TradeMessage());
	}

	@Test(enabled=true,groups="ejbexc",
			expectedExceptions=BadArgumentsException.class,expectedExceptionsMessageRegExp="Wrong user ID: -1")
	public void wrongUserId() throws ProcessingException {
		TradeMessage m = TestUtil.makeMessage();
		m.setUserId(-1);
		service.add(m);
	}
	
	@Test(enabled=true,groups="ejbexc",
			expectedExceptions=BadArgumentsException.class,expectedExceptionsMessageRegExp="No records found.*")
	public void emptyList() {
		service.getList();
	}
	
	@Test(enabled=true,dependsOnGroups="ejbexc",groups="ejb")
	public void addedOK() throws ProcessingException {
			
		TradeMessage m = TestUtil.makeMessage();
		service.addAsync(m);
		try {
			Thread.sleep(200);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		List<TradeMessage> list = service.getList();
		list = service.getList();
		
		assertEquals(list.size(), 1);
		assertEquals(list.get(0), m);

	}
	
	@Test(enabled=true,
			dependsOnGroups="ejb",
			groups="restexc")
	public void restExceptions() throws URISyntaxException, IOException {
		service.clear();
		Response r = getClient().path("list").get();
		assertEquals(r.getStatus(), 422);
		ExceptionResponse eres = jsonProvider.readEntity((InputStream) r.getEntity(), ExceptionResponse.class);
		assertEquals(eres.getMessage(), "No records found");
		assertNull(eres.getCauseMessage());
		assertEquals(eres.getExceptionClass(), BadArgumentsException.class.getName());
		assertNull(eres.getStackTrace());
		
		//---- processing exception
		{
			TradeMessage m = TestUtil.makeMessage();
			m.setOriginatingCountry("TZ");
			
			r = client.replacePath("add").post(m);
			assertEquals(r.getStatus(), 500);
			eres = jsonProvider.readEntity((InputStream) r.getEntity(), ExceptionResponse.class);
			assertEquals(eres.getMessage(), "Cannot process origin country: TZ");
			assertEquals(eres.getCauseMessage(), "For input string: \"TZ\"");
			assertEquals(eres.getExceptionClass(), NumberFormatException.class.getName());
			assertNotNull(eres.getStackTrace());
			assertTrue(eres.getStackTrace().length > 0);
		}
		
		//---- unexpected exception
		{
			TradeMessage m = TestUtil.makeMessage();
			m.setAmountBuy(BigDecimal.TEN);
			r = client.post(m);
			assertEquals(r.getStatus(), 500);
			eres = jsonProvider.readEntity((InputStream) r.getEntity(), ExceptionResponse.class);
			assertEquals(eres.getMessage(), "Division by zero");
			assertNull(eres.getCauseMessage());
			assertEquals(eres.getExceptionClass(), ArithmeticException.class.getName());
			assertNotNull(eres.getStackTrace());
			assertTrue(eres.getStackTrace().length > 0);
		}		
		
		//---- bad arguments exceptions
		{
			TradeMessage m = TestUtil.makeMessage();
			m.setAmountBuy(null);
			r = client.post(m);
			assertEquals(r.getStatus(), 422);
			eres = jsonProvider.readEntity((InputStream) r.getEntity(), ExceptionResponse.class);
			assertEquals(eres.getMessage(), "Wrong buy amount: null");
			assertNull(eres.getCauseMessage());
			assertEquals(eres.getExceptionClass(), "com.cfair.trial.util.BadArgumentsException");
			assertNull(eres.getStackTrace());
			
			
			m.setAmountBuy(BigDecimal.valueOf(1));
			m.setUserId(-1);
			r = client.post(m);
			assertEquals(r.getStatus(), 422);
			eres = jsonProvider.readEntity((InputStream) r.getEntity(), ExceptionResponse.class);
			assertEquals(eres.getMessage(), "Wrong user ID: -1");
			assertNull(eres.getCauseMessage());
			assertEquals(eres.getExceptionClass(), BadArgumentsException.class.getName());
			assertNull(eres.getStackTrace());
		}
	}
	
	@Test(enabled=true,dependsOnGroups="restexc",groups="wcrest")
	public void restAddedOK() throws IOException, InterruptedException {
		TradeMessage m = TestUtil.makeMessage();
		Response r = client.replacePath("addasync").post(m);
		assertEquals(r.getStatus(), 204);
		
		//processed acynsly, so wait a bit
		Thread.sleep(200);
		
		List<TradeMessage> list = new ArrayList<>(client.replacePath("list").getCollection(TradeMessage.class));
		assertEquals(list.size(), 1);
		assertEquals(list.get(0), m);
	}
	
	@SuppressWarnings("serial")
	@Test(enabled=true,dependsOnGroups="wcrest",groups="htexc")
	public void htrestExceptions() throws ClientProtocolException, IOException {
		service.clear();
		
		HttpPost post = new HttpPost(webappUrl.toString() + "service/add");
		post.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		String body;
		HttpResponse htr;
		
		body = TestUtil.buildTradeMessageJson(new HashMap<String, Object>(){{
			put("amountBuy", null);
		}});
		post.setEntity(new StringEntity(body));
		htr = htclient.execute(post);
		assertEquals(htr.getStatusLine().getStatusCode(), 422);
		ExceptionResponse eres = jsonProvider.readEntity(htr.getEntity().getContent(), ExceptionResponse.class);
		assertEquals(eres.getMessage(), "Wrong buy amount: null");
		assertNull(eres.getCauseMessage());
		assertEquals(eres.getExceptionClass(), "com.cfair.trial.util.BadArgumentsException");
		assertNull(eres.getStackTrace());

		body = TestUtil.buildTradeMessageJson(new HashMap<String, Object>(){{
			put("amountBuy", 10);
		}});
		post.setEntity(new StringEntity(body));
		htr = htclient.execute(post);
		assertEquals(htr.getStatusLine().getStatusCode(), 500);
		eres = jsonProvider.readEntity(htr.getEntity().getContent(), ExceptionResponse.class);
		assertEquals(eres.getMessage(), "Division by zero");
		assertNull(eres.getCauseMessage());
		assertEquals(eres.getExceptionClass(), ArithmeticException.class.getName());
		assertNotNull(eres.getStackTrace());
		assertTrue(eres.getStackTrace().length > 0);

		body = TestUtil.buildTradeMessageJson(new HashMap<String, Object>(){{
			put("userId", "aaa");
		}});
		post.setEntity(new StringEntity(body));
		htr = htclient.execute(post);
		assertEquals(htr.getStatusLine().getStatusCode(), 422);
		eres = jsonProvider.readEntity(htr.getEntity().getContent(), ExceptionResponse.class);
		assertEquals(eres.getMessage(), "Inconsistent value of [userId]");
		assertNull(eres.getCauseMessage());
		assertEquals(eres.getExceptionClass(), InvalidFormatException.class.getName());
		assertNull(eres.getStackTrace());
		
		body = TestUtil.buildTradeMessageJson(new HashMap<String, Object>(){{
			put("currencyFrom", "ZZZ");
		}});
		post.setEntity(new StringEntity(body));
		htr = htclient.execute(post);
		assertEquals(htr.getStatusLine().getStatusCode(), 422);
		eres = jsonProvider.readEntity(htr.getEntity().getContent(), ExceptionResponse.class);
		assertEquals(eres.getMessage(), "Inconsistent value of [currencyFrom]");
		assertNull(eres.getCauseMessage());
		assertEquals(eres.getExceptionClass(), InvalidFormatException.class.getName());
		assertNull(eres.getStackTrace());
		
	}
	
	@Test(enabled=true,dependsOnGroups="htexc",groups="resttest")
	public void htrestAddedOK() throws ClientProtocolException, IOException, InterruptedException {
		HttpPost post = new HttpPost(webappUrl.toString() + "service/addasync");
		post.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		
		String body = TestUtil.buildTradeMessageJson(Collections.<String, Object>emptyMap());
		post.setEntity(new StringEntity(body));
		HttpResponse htr = htclient.execute(post);
		assertEquals(htr.getStatusLine().getStatusCode(), 204);
		
		//processed asyncly, so wait a bit
		Thread.sleep(100);
		
		HttpGet get = new HttpGet(webappUrl.toString() + "service/list");
		get.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
		htr = htclient.execute(get);
		assertEquals(htr.getStatusLine().getStatusCode(), 200);
		List<TradeMessage> list = jsonProvider.readList(htr.getEntity().getContent(), TradeMessage.class);
		assertEquals(list.size(), 1);
		
		TradeMessage m = list.get(0);
		assertEquals(m.getUserId(), 134256);
		assertEquals(m.getCurrencyFrom(), Currency.getInstance("EUR"));
		assertEquals(m.getCurrencyTo(), Currency.getInstance("GBP"));
		assertEquals(m.getAmountSell(), BigDecimal.valueOf(1000));
		assertEquals(m.getAmountBuy(), BigDecimal.valueOf(747.10));
		assertEquals(m.getRate(), BigDecimal.valueOf(.7471));
		assertEquals(m.getOriginatingCountry(), "FR");
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.set(2015, Calendar.JANUARY, 14, 10, 27, 44);
		c.set(Calendar.MILLISECOND, 0);
		assertEquals(m.getTimePlaced(), c.getTime());
	}
	
}
