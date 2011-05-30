/**
 * AbstractNetFlowCollector.java 08.01.2007
 */
package org.dicr.netflow.collector.impl;

import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.collector.*;
import org.dicr.netflow.impl.v1.*;
import org.dicr.netflow.impl.v5.*;
import org.dicr.netflow.impl.v6.*;
import org.dicr.netflow.impl.v7.*;
import org.dicr.netflow.impl.v8.*;
import org.dicr.netflow.packet.*;

/**
 * Abstract NetFlow Collector.
 * <P>
 * Implements common used methods
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public abstract class AbstractNetFlowCollector implements NetFlowCollector, PacketSource {
	/** Logger */
	private static final Logger log = Logger.getLogger(AbstractNetFlowCollector.class);

	/** Packet Listeners */
	private final Set<PacketListener> listeners = new HashSet<PacketListener>();

	/** Skip empty packets */
	private boolean skipEmpty = true;

	/** Register known types */
	static {
		FlowType.registerType(FlowTypeV1.VERSION, FlowTypeV1.INSTANCE);
		FlowType.registerType(FlowTypeV5.VERSION, FlowTypeV5.INSTANCE);
		FlowType.registerType(FlowTypeV6.VERSION, FlowTypeV6.INSTANCE);
		FlowType.registerType(FlowTypeV7.VERSION, FlowTypeV7.INSTANCE);
		FlowType.registerType(FlowTypeV8.VERSION, FlowTypeV8.AS);
	}

	/** Constructor */
	protected AbstractNetFlowCollector() {
		super();
	}

	/**
	 * @see org.dicr.netflow.packet.PacketSource#setPacketListeners(java.util.Set)
	 */
	public void setPacketListeners(final Set<PacketListener> packetListeners) {
		synchronized (this.listeners) {
			this.listeners.clear();
			if (packetListeners != null) this.listeners.addAll(packetListeners);
		}
		AbstractNetFlowCollector.log.debug("configured " + (packetListeners != null ? packetListeners.size() : 0)
		        + " NetFlow packet listeners");
	}

	/**
	 * @see org.dicr.netflow.packet.PacketSource#addListener(org.dicr.netflow.packet.PacketListener)
	 */
	public void addListener(final PacketListener listener) {
		if (listener == null) throw new IllegalArgumentException("null listener");
		synchronized (this.listeners) {
			this.listeners.add(listener);
		}
	}

	/**
	 * @see org.dicr.netflow.packet.PacketSource#removeListener(org.dicr.netflow.packet.PacketListener)
	 */
	public void removeListener(final PacketListener listener) {
		if (listener == null) throw new IllegalArgumentException("null listener");
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Set skip empty flag
	 * 
	 * @param skip true, to not fire empty packets (withowt flows) to listeners
	 */
	public void setSkipEmpty(final boolean skip) {
		this.skipEmpty = skip;
	}

	/**
	 * Return skip empty flag
	 * 
	 * @return true if empty packets (withowt flows) not fired to listeners
	 */
	public boolean isSkipEmpty() {
		return this.skipEmpty;
	}

	/**
	 * Fire received packet to flow listeners.
	 * 
	 * @param packet received packet
	 */
	protected void firePacket(final NetFlowPacket packet) {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (packet.getFlowsCount() < 1 && this.skipEmpty) {
			AbstractNetFlowCollector.log.trace("skipping empty packet");
			return;
		}
		synchronized (this.listeners) {
			if (this.listeners.isEmpty()) AbstractNetFlowCollector.log.trace("no listeners");
			else {
				AbstractNetFlowCollector.log.trace("firing packet:\n" + packet + " to " + this.listeners.size()
				        + " listeners");
				for (final PacketListener listener : this.listeners) {
					try {
						listener.processPacket(packet);
					} catch (final Exception ex) {
						AbstractNetFlowCollector.log.error("error invoking listener: " + listener, ex);
					}
				}
			}
		}
	}
}
