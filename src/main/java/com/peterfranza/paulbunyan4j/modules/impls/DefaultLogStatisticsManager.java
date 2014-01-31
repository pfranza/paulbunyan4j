package com.peterfranza.paulbunyan4j.modules.impls;

import java.util.concurrent.ExecutionException;

import javax.inject.Singleton;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.peterfranza.paulbunyan4j.messages.Messages.LoggingMessage;

@Singleton
public class DefaultLogStatisticsManager {

	LoadingCache<String, StatSample> graphs = CacheBuilder.newBuilder()
			.maximumSize(10000).build(new CacheLoader<String, StatSample>() {
			public StatSample load(String key)  {
				return new StatSample().setLabel(key);
			}
	});
	
	public void processMessage(LoggingMessage msg) {
		try {
			graphs.get(msg.getApplicationid() + "/" + msg.getApplicationinstanceid() + "/" +msg.getLabel()).addSample(msg.getDuration());
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	private static class StatSample {
		String label;
		
		long sampleSum;
		long sampleCount;
		
		long lastValue;
		long minValue;
		long maxValue;
		
		StatSample setLabel(String l) {
			label = l;
			return this;
		}
		
		StatSample addSample(long d) {
			sampleSum += d;
			sampleCount += 1;
			lastValue = d;
			minValue = Math.min(minValue, d);
			maxValue = Math.max(maxValue, d);
			
			return this;
		}
	}

}
