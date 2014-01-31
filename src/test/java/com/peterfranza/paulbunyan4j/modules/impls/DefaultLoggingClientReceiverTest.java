package com.peterfranza.paulbunyan4j.modules.impls;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.modules.LoggingClientModule;
import com.peterfranza.paulbunyan4j.modules.impls.DefaultLoggingClientSenderTest.DefaultLoggingClientSenderTestSample;

public class DefaultLoggingClientReceiverTest {

	@Test
	public void test() throws Exception {
		Injector i = Guice.createInjector(new LoggingClientModule("127.0.0.1", 9546, "DefaultLoggingClientSenderTest", "1", "localhost"));
		
		DefaultLoggingClientSender cRcv = i.getInstance(DefaultLoggingClientSender.class);
		LoggingClient.LoggingSender m = i.getInstance(DefaultLoggingClientSender.class);
		assertNotNull(m);
		assertNotNull(cRcv);
		
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
		i.getInstance(DefaultLoggingClientSenderTestSample.class).callable();
		Thread.sleep(500);
		assertEquals(1, cRcv.getMessageCount());
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
	}

}
