package com.peterfranza.paulbunyan4j.modules.impls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.modules.LoggingCentralModule;
import com.peterfranza.paulbunyan4j.modules.LoggingClientModule;
import com.peterfranza.paulbunyan4j.modules.impls.DefaultLoggingClientSenderTest.DefaultLoggingClientSenderTestSample;

public class DefaultLoggingClientReceiverTest {

	@Test
	public void test() throws Exception {
		final Injector si = Guice.createInjector(new LoggingClientModule("127.0.0.1", 9546, "DefaultLoggingClientSenderTest", "1", "localhost"));
		final Injector ri = Guice.createInjector(new LoggingCentralModule(9546, "DefaultLoggingClientSenderTest", "1", "localhost"));
		
		DefaultLoggingClientReceiver cRcv = ri.getInstance(DefaultLoggingClientReceiver.class);
		LoggingClient.LoggingSender m = si.getInstance(LoggingClient.LoggingSender.class);
		assertNotNull(m);
		assertNotNull(cRcv);
		Thread.sleep(50);
		
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
	
		si.getInstance(DefaultLoggingClientSenderTestSample.class).callable();
		
		Thread.sleep(500);
		assertEquals(1, cRcv.getRemoteMessageCount());
		assertEquals(m.getMessageCount(), cRcv.getRemoteMessageCount());
	}
	
	
}
