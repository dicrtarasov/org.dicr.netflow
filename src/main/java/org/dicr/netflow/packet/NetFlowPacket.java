package org.dicr.netflow.packet;

import java.util.*;

import org.dicr.netflow.exc.*;
import org.dicr.traffic.source.*;

/**
 * Abstract NetFlow Packet.
 * 
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public abstract class NetFlowPacket {
	/** Maximum value of SysUptime */
	public static final long UPTIME_VALUE_MAX = 0x0FFFFFFFFL;

	/** Maximum value of UnixSecs */
	public static final long UNIX_SECS_MAX = 0x0FFFFFFFFL;

	/** Current time in milliseconds since the export device booted */
	private long sysUptime = 0;

	/** Current count of seconds since 0000 UTC 1970 */
	private long unixSecs = System.currentTimeMillis() / 1000;

	/** Flows */
	private final Collection<Flow> flows = new ArrayList<Flow>();

	/**
	 * Constructor
	 */
	protected NetFlowPacket() {
		super();
	}

	/**
	 * Return flow type
	 * 
	 * @return netflow type descriptor object
	 */
	public abstract FlowType getFlowType();

	/**
	 * Set SysUptime.
	 * 
	 * @param millis current time in milliseconds since the export device booted
	 * @see #UPTIME_VALUE_MAX
	 */
	public void setSysUptime(final long millis) {
		if (millis < 0 || millis > NetFlowPacket.UPTIME_VALUE_MAX) throw new IllegalArgumentException("uptime: "
		        + millis);
		this.sysUptime = millis;
	}

	/**
	 * Return SysUptime.
	 * 
	 * @return current time in milliseconds since the export device booted.
	 */
	public long getSysUptime() {
		return this.sysUptime;
	}

	/**
	 * Set UnixSecs.
	 * 
	 * @param seconds current count of seconds since 0000 UTC 1970
	 * @see #UNIX_SECS_MAX
	 */
	public void setUnixSecs(final long seconds) {
		if (seconds < 0 || seconds > NetFlowPacket.UNIX_SECS_MAX) throw new IllegalArgumentException("secs: " + seconds);
		this.unixSecs = seconds;
	}

	/**
	 * Return UnixSecs.
	 * 
	 * @return current count of seconds since 0000 UTC 1970
	 */
	public long getUnixSecs() {
		return this.unixSecs;
	}

	/**
	 * Return flows count.
	 * 
	 * @return current count of flows in packet.
	 */
	public int getFlowsCount() {
		synchronized (this.flows) {
			return this.flows.size();
		}
	}

	/**
	 * Return flows.
	 * 
	 * @return flows of packet.
	 */
	public Collection<Flow> getFlows() {
		synchronized (this.flows) {
			return new ArrayList<Flow>(this.flows);
		}
	}

	/**
	 * Add flow to packet. SysUptime updated by maximum flow uptime.
	 * 
	 * @param flow flow to add
	 * @throws NetFlowException if number of flows exceed maximum
	 */
	public void addFlow(final Flow flow) throws NetFlowException {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!this.getFlowType().equals(flow.getFlowType())) throw new NetFlowException("packet flow type '"
		        + this.getFlowType() + " does not match adding flow type: " + flow.getFlowType());
		synchronized (this.flows) {
			if (this.flows.size() >= this.getFlowType().getMaxFlowsCount()) throw new NetFlowException(
			        "count of flows is already maximum: " + this.getFlowsCount());
			this.flows.add(flow);
			if (this.sysUptime < flow.getLast()) this.sysUptime = flow.getLast();
		}
	}

	/**
	 * Convert to traffic
	 * 
	 * @return collection of traffic elements from this packet
	 */
	public Collection<TrafficElement> toTraffic() {
		final Collection<TrafficElement> traffic = new ArrayList<TrafficElement>();
		final long bootTime = this.unixSecs * 1000 - this.sysUptime;
		for (final Flow flow : this.flows) {
			traffic.add(flow.toTrafficElement(bootTime));
		}
		return traffic;
	}

	/**
	 * Convert to string
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append("{");
		sb.append("type=").append(this.getFlowType()).append("\n");
		final Iterator<Flow> flowIterator = this.flows.iterator();
		while (flowIterator.hasNext()) {
			sb.append(flowIterator.next().toString());
			if (flowIterator.hasNext()) sb.append(",\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
