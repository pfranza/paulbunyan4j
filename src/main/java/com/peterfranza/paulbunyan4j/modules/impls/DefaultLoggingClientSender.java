package com.peterfranza.paulbunyan4j.modules.impls;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.messages.Messages;

@Singleton
public class DefaultLoggingClientSender implements LoggingClient.LoggingSender {

	@Inject DatagramSocket clientSocket;
	@Inject @Named("LoggingEndpoint") InetAddress serviceAddress;
	@Inject @Named("servicePort") int servicePort = 9876;
	
	private long messageCount = 0;

	{
		System.out.println("Using DefaultLoggingClientSender");
	}
	
	public void send(Messages.LoggingMessage message) {
		try {
			sendMessage(message.toByteArray());
		} catch (IOException e) {
			System.err.println("Error Sending Log Entry");
		}
	}
	
	private void sendMessage(byte[] sendData) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serviceAddress, servicePort); 
		clientSocket.send(sendPacket);
		messageCount += 1;
	}
	
	public long getMessageCount() {
		return messageCount;
	}
	
}
