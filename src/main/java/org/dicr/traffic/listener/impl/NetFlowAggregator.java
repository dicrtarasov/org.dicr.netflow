/**
 * NetFlowAggregator.java 03.01.2007
 */
package org.dicr.traffic.listener.impl;

import org.apache.log4j.*;
import org.dicr.netflow.cache.*;
import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * Aggregate Traffic to NetFlow Cache
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070103
 */
public class NetFlowAggregator implements TrafficListener {
	private static final Logger log = Logger.getLogger(NetFlowAggregator.class);

	/** Flow cache */
	private FlowCache cache = null;

	/** Constructor */
	public NetFlowAggregator() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param flowCache NetFlow cache to use
	 */
	public NetFlowAggregator(final FlowCache flowCache) {
		super();
		this.setCache(flowCache);
	}

	/**
	 * Set cache
	 * 
	 * @param flowCache NetFlow cache to use for aggregation
	 */
	public void setCache(final FlowCache flowCache) {
		if (flowCache == null) throw new IllegalArgumentException("null cache");
		synchronized (this) {
			this.cache = flowCache;
		}
		NetFlowAggregator.log.debug("configured flow cache: " + flowCache);
	}

	/**
	 * Return flow cache
	 * 
	 * @return flow cache
	 */
	public FlowCache getCache() {
		return this.cache;
	}

	/**
	 * Process events from sources.
	 * 
	 * @param e event from source
	 * @see org.dicr.traffic.source.TrafficListener#processTrafficEvent(org.dicr.traffic.source.TrafficEvent)
	 */
	public final void processTrafficEvent(final TrafficEvent e) {
		if (e == null) throw new IllegalArgumentException("null event");
		synchronized (this.cache) {
			if (this.cache == null) throw new IllegalArgumentException("cache not configured");
			final FlowType type = this.cache.getFlowType();
			if (type == null) throw new IllegalStateException("cache flow type not configured");
			for (final TrafficElement element : e.getTraffic())
				this.cache.accumulate(type.createFlow(element));
		}
	}
}
