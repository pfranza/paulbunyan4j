package com.peterfranza.paulbunyan4j.modules.impls;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

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
	
	LoadingCache<String, StatSample> graphs = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.build(new CacheLoader<String, StatSample>() {
			public StatSample load(String key)  {
				return new StatSample().setLabel(key);
			}
	});
	
	public void processMessage(LoggingMessage msg) {
		try {
			graphs.get(msg.getApplicationid() + "/" + msg.getApplicationinstanceid() + "/" +msg.getLabel()).addSample(msg.getDuration(), msg.getTimestamp());
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

}
