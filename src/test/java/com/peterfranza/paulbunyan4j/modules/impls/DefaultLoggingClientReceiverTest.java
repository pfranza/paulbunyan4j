package com.peterfranza.paulbunyan4j.modules.impls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;

import javax.security.auth.Subject;

import org.junit.Test;
import org.junit.internal.runners.statements.RunAfters;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.modules.LoggingClientModule;
import com.peterfranza.paulbunyan4j.modules.impls.DefaultLoggingClientSenderTest.DefaultLoggingClientSenderTestSample;

public class DefaultLoggingClientReceiverTest {

	@Test
	public void test() throws Exception {
		final Injector i = Guice.createInjector(new LoggingClientModule("127.0.0.1", 9546, "DefaultLoggingClientSenderTest", "1", "localhost"));
		
		DefaultLoggingClientReceiver cRcv = i.getInstance(DefaultLoggingClientReceiver.class);
		LoggingClient.LoggingSender m = i.getInstance(DefaultLoggingClientSender.class);
		assertNotNull(m);
		assertNotNull(cRcv);
		Thread.sleep(50);
		
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
	
		Subject.doAs(new Subject(true, new HashSet<Principal>(Arrays.asList(new com.sun.security.auth.UserPrincipal("Self"))), new HashSet<Object>(), new HashSet<Object>()), 
				new PrivilegedAction<Void>() {

			public Void run() {
				i.getInstance(DefaultLoggingClientSenderTestSample.class).callable();
				return null;
			}
		});
		
		Thread.sleep(500);
		assertEquals(1, cRcv.getMessageCount());
		assertEquals(m.getMessageCount(), cRcv.getMessageCount());
	}
	
	
}
