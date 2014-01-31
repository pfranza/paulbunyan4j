package com.peterfranza.paulbunyan4j.modules.impls;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.protobuf.ByteString;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

@Singleton
public class DefaultLoggingClientReceiver implements Runnable {

	private DatagramSocket serverSocket;
	private long messageCount = 0;
	
	@Inject DefaultLogStatisticsManager statisticsManager;

	@Inject
	DefaultLoggingClientReceiver(@Named("servicePort") int servicePort) throws SocketException {
		serverSocket = new DatagramSocket(servicePort);
		new Thread(this){{setDaemon(true);}}.start();
	}
	
	public final void run() {
		
		byte[] receiveData = new byte[1500];
		try {
			while(true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				LoggingMessage msg = LoggingMessage.parseFrom(ByteString.copyFrom(receiveData, 0, receivePacket.getLength()));
				statisticsManager.processMessage(msg);
				processMessage(msg);
				messageCount += 1;
			}	
		} catch(IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
	
	protected void processMessage(LoggingMessage msg) {}
	
	public long getMessageCount() {
		return messageCount;
	}

	
}
