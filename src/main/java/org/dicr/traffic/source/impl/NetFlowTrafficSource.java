/**
 * NetFlowTrafficSource.java 08.01.2007
 */
package org.dicr.traffic.source.impl;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * NetFlow Traffic Source.
 * <P>
 * Wrap NetFlow {@link PacketSource packet source} to convert {@link NetFlowPacket}s to {@link TrafficElement}s
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public class NetFlowTrafficSource extends AbstractTrafficSource implements PacketListener {
	/** Source */
	private PacketSource source = null;

	/**
	 * Constructor
	 */
	public NetFlowTrafficSource() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param packetSource source of NetFlow packets
	 */
	public NetFlowTrafficSource(PacketSource packetSource) {
		super();
		this.setSource(packetSource);
	}

	/**
	 * Set sources
	 * 
	 * @param packetSource source of NetFlow packets
	 */
	public void setSource(PacketSource packetSource) {
		synchronized (this) {
			if (this.source != null) this.source.removeListener(this);
			this.source = packetSource;
			if (this.source != null) this.source.addListener(this);
		}
	}

	/**
	 * Process netFlow packet.
	 * <P>
	 * Convert {@link NetFlowPacket} to {@link TrafficElement}s and fire it to
	 * {@link #setTrafficListeners(java.util.Set) listeners}.
	 * 
	 * @param packet packet to handle
	 */
	public void processPacket(NetFlowPacket packet) {
		if (packet == null) throw new IllegalArgumentException("null packet"); //$NON-NLS-1$
		this.fireTraffic(packet.toTraffic());
	}
}
