package com.peterfranza.paulbunyan4j.modules.impls;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.peterfranza.paulbunyan4j.LoggingClient;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingStatisticsSample;
import com.peterfranza.paulbunyan4j.LoggingClient.LoggingStatisticsSamplesVisitor;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

@Singleton
public class DefaultLogStatisticsManager implements LoggingClient.LoggingStatistics {

	
	
	@Inject
	@Named("message_retention_time")
	private long messageRetentionTime;
	
	private long lastValue;
	
	LoadingCache<String, StatSample> graphs = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<String, StatSample>() {
			public StatSample load(String key)  {
				return new StatSample().setLabel(key);
			}
	});
	
	Cache<String, LoggingMessage> messages;
	
	public synchronized void processMessage(LoggingMessage msg) {
		try {
			
			if(messages == null) {
				messages = CacheBuilder.newBuilder()
						.maximumSize(10000)
						.expireAfterWrite(messageRetentionTime, TimeUnit.MILLISECONDS)
						.build();
			}
			
			graphs.get(msg.getApplicationid() + " / " + msg.getApplicationinstanceid() + " / " +msg.getLabel()).addSample(msg.getDuration(), msg.getTimestamp());
			messages.put(msg.getApplicationid() + " / " + msg.getUsername(), msg);
			lastValue = msg.getDuration();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void getSamples(LoggingStatisticsSamplesVisitor visitor) {
		for(StatSample s: graphs.asMap().values()) {
			visitor.visit(s);
		}
	}

	
	private static class StatSample implements LoggingClient.LoggingStatisticsSample {
		String label;
		
		long sampleSum;
		long sampleCount;
		
		long lastValue;
		long minValue;
		long maxValue;
		
		long firstTimestamp = -1;
		long lastTimestamp;
		
		StatSample setLabel(String l) {
			label = l;
			return this;
		}
		
		LoggingStatisticsSample addSample(long d, long timestamp) {
			sampleSum += d;
			sampleCount += 1;
			lastValue = d;
			minValue = Math.min(minValue, d);
			maxValue = Math.max(maxValue, d);
			
			lastTimestamp = Math.max(lastTimestamp, timestamp);
			if(firstTimestamp == -1) {
				firstTimestamp = timestamp;
			}
			
			return this;
		}
		
		@Override
		public String getLabel() {
			return label;
		}
		
		@Override
		public double getAverage() {
			return getTotal() / sampleCount;
		}
		
		@Override
		public double getTotal() {
			return sampleSum;
		}
		
		@Override
		public long getMinValue() {
			return minValue;
		}
		
		@Override
		public long getMaxValue() {
			return maxValue;
		}
		
		@Override
		public long getLastValue() {
			return lastValue;
		}
	}


	@Override
	public String getReport() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" rules=\"all\">");
		
		buffer.append("<tr>")
			.append("<th>").append("Label").append("</th>")
			.append("<th>").append("Hits").append("</th>")
			.append("<th>").append("Average").append("</th>")
			.append("<th>").append("Total").append("</th>")
			.append("<th>").append("Last").append("</th>")
			.append("<th>").append("Min").append("</th>")
			.append("<th>").append("Max").append("</th>")
			.append("<th>").append("First").append("</th>")
			.append("<th>").append("Last").append("</th>")
			.append("</tr>");
		
		for(StatSample s: graphs.asMap().values()) {
			buffer.append("<tr>")
			.append("<td>").append(s.label).append("</td>")
			.append("<td>").append(s.sampleCount).append("</td>")
			.append("<td>").append(s.getAverage()).append("</td>")
			.append("<td>").append(s.getTotal()).append("</td>")
			.append("<td>").append(s.getLastValue()).append("</td>")
			.append("<td>").append(s.getMinValue()).append("</td>")
			.append("<td>").append(s.getMaxValue()).append("</td>")
			.append("<td>").append(new Date(s.firstTimestamp)).append("</td>")
			.append("<td>").append(new Date(s.lastTimestamp)).append("</td>")
			.append("</tr>");
		}
		
		HashMap<String, StringBuffer> usernames = new HashMap<String, StringBuffer>();
		for(LoggingMessage m: messages.asMap().values()) {
			StringBuffer buf = usernames.get(m.getUsername());
			if(buf == null){buf = new StringBuffer(); usernames.put(m.getUsername(), buf);}
			if(!buf.toString().toLowerCase().contains(m.getApplicationid())) {
				if(buf.length() > 0) {
					buf.append(", ");
				}
				buf.append(m.getApplicationid());
			}
		}
		buffer.append("</table>");
		buffer.append("<table border=\"1\" rules=\"all\">");
		
		buffer.append("<tr>")
			.append("<th>").append("Username").append("</th>")
			.append("<th colspan=\"8\">").append("Applications").append("</th>")
			.append("</tr>");
		
		for (Entry<String, StringBuffer> entry : usernames.entrySet()) {
			buffer.append("<tr>")
				.append("<td>").append(entry.getKey()).append("</td>")
				.append("<td colspan=\"8\">").append(entry.getValue().toString()).append("</td>")
			.append("</tr>");
		}
		
		buffer.append("</table>");
		return buffer.toString();
	}


	@Override
	public int getSamples() {
		int c = 0;
		for(StatSample s: graphs.asMap().values()) {
			c += s.sampleCount;
		}
		return c;
	}


	@Override
	public double getAverage() {
		double sum = 0;
		double samples = 0;
		for(StatSample s: graphs.asMap().values()) {
			sum += s.sampleSum;
			samples += s.sampleCount;
		}
		return sum / samples;
	}


	@Override
	public long getLastValue() {
		return lastValue;
	}

}
