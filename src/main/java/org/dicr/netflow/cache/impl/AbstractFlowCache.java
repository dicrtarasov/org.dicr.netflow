/**
 * AbstractFlowCache.java 09.01.2007
 */
package org.dicr.netflow.cache.impl;

import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.cache.*;
import org.dicr.netflow.packet.*;

/**
 * Abstract Flow Cache. Implement common methods of flow cache.
 * <P>
 * Cache {@link #setFlowType(FlowType) flow type} must be configured before using cache.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public abstract class AbstractFlowCache implements FlowCache, PacketListener, FlowListener, FlowSource {
	/** Logger */
	private static final Logger log = Logger.getLogger(AbstractFlowCache.class);

	/** Listeners */
	private final Set<FlowListener> listeners = new HashSet<FlowListener>();

	/** Flow Type */
	private FlowType flowType = null;

	/**
	 * Constructor
	 */
	protected AbstractFlowCache() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param type cache flow type
	 */
	protected AbstractFlowCache(final FlowType type) {
		super();
		this.setFlowType(type);
	}

	/**
	 * Set cache flow type.
	 * <P>
	 * Cache type must be configured ONCE, before cache usage and first flow accumulated.
	 * </P>
	 * 
	 * @param type type of cache flow
	 */
	public void setFlowType(final FlowType type) {
		if (type == null) throw new IllegalArgumentException("null flow flowType");
		synchronized (this) {
			if (this.flowType != null) throw new IllegalStateException("flow type already configured");
			this.flowType = type;
		}
		AbstractFlowCache.log.debug("configured flow type: " + type);
	}

	/**
	 * @see org.dicr.netflow.cache.FlowCache#getFlowType()
	 */
	public FlowType getFlowType() {
		synchronized (this) {
			return this.flowType;
		}
	}

	/**
	 * Set listeners of expired flows.
	 * 
	 * @see org.dicr.netflow.packet.FlowSource#setFlowListeners(java.util.Set)
	 */
	public void setFlowListeners(final Set<FlowListener> flowListeners) {
		synchronized (this.listeners) {
			this.listeners.clear();
			if (flowListeners != null) this.listeners.addAll(flowListeners);
		}
		AbstractFlowCache.log.debug("configured " + (flowListeners != null ? flowListeners.size() : 0) + " listeners");
	}

	/**
	 * Add listener of expired flows
	 * 
	 * @see org.dicr.netflow.packet.FlowSource#addListener(org.dicr.netflow.packet.FlowListener)
	 */
	public void addListener(final FlowListener listener) {
		if (listener == null) throw new IllegalArgumentException("null listener");
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
		AbstractFlowCache.log.debug("added listener: " + listener);
	}

	/**
	 * Remove listener of expired flows
	 * 
	 * @see org.dicr.netflow.packet.FlowSource#removeListener(org.dicr.netflow.packet.FlowListener)
	 */
	public void removeListener(final FlowListener listener) {
		if (listener == null) throw new IllegalArgumentException("null listener");
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
		AbstractFlowCache.log.debug("removed listener: " + listener);
	}

	/**
	 * Fire expired flows to listeners
	 * 
	 * @param flows flows to fire
	 */
	protected void fireFlows(final Collection<? extends Flow> flows) {
		if (flows == null) throw new IllegalArgumentException("null flows to fire");
		if (flows.isEmpty()) return;
		synchronized (this.listeners) {
			if (this.listeners.isEmpty()) AbstractFlowCache.log.debug("no listeners");
			else {
				AbstractFlowCache.log.trace("firing " + flows.size() + " expired flows to " + this.listeners.size()
				        + " listeners");
				for (final FlowListener listener : this.listeners)
					try {
						listener.processFlows(flows);
					} catch (final Exception ex) {
						AbstractFlowCache.log.error("error invoking flow listener " + listener, ex);
					}
			}
		}
	}

	/**
	 * Accumulate flows to cache
	 * 
	 * @see org.dicr.netflow.packet.FlowListener#processFlows(java.util.Collection)
	 */
	public void processFlows(final Collection<? extends Flow> flows) {
		if (flows == null) throw new IllegalArgumentException("null flows");
		if (flows.isEmpty()) return;
		for (final Flow flow : flows) {
			this.accumulate(flow);
		}
		AbstractFlowCache.log.trace("accumulated " + flows.size() + " flows");
	}

	/**
	 * Accumulate flows from packet to cache
	 * 
	 * @see org.dicr.netflow.packet.PacketListener#processPacket(org.dicr.netflow.packet.NetFlowPacket)
	 */
	public void processPacket(final NetFlowPacket packet) {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (packet.getFlowsCount() < 1) return;
		this.processFlows(packet.getFlows());
	}
}
