/**
 * Flow.java
 */
package org.dicr.netflow.packet;

import org.dicr.traffic.source.*;

/**
 * Abstract Flow.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060122
 */
public abstract class Flow {
	/**
     * Boot time of this flow router (in milliseconds)
     *
     * @see System#currentTimeMillis()
     */
	public static final long bootTime = System.currentTimeMillis();

	/** Maximum value of packets count */
	public static final long MAX_PACKETS = 0x0FFFFFFFFL;

	/** Maximum value of octets count */
	public static final long MAX_BYTES = 0x0FFFFFFFFL;

	/** Maximum value of last packet uptime. */
	public static final long MAX_UPTIME = 0x0FFFFFFFFL;

	/** Packets in the flow */
	private long packets = 0;

	/** Total number of Layer 3 octets in the packets of the flow */
	private long octets = 0;

	/** SysUptime at start of flow */
	private long first = 0;

	/** SysUptime at the time the last packet of the flow was received */
	private long last = 0;

	/**
     * Constructor.
     */
	protected Flow() {
		super();
	}

	/**
     * Init values from traffic element. Need to overwrite.
     *
     * @param element traffic element to get values
     */
	protected Flow(TrafficElement element) {
		super();
		if (element == null) throw new IllegalArgumentException("null element");
		long uptime = element.getTime() - bootTime;
		this.first = uptime;
		this.last = uptime;
		this.packets = element.getPackets() != null ? element.getPackets().longValue() : 0;
		this.octets = element.getBytes();
	}

	/**
     * Return flow type
     *
     * @return flow type of this flow
     */
	public abstract FlowType getFlowType();

	/**
     * Set number of packets.
     *
     * @param count packet count in the flow
     * @see #MAX_PACKETS
     */
	public final void setPacketsCount(long count) {
		if (count < 0 || count > MAX_PACKETS) throw new IllegalArgumentException("count: " + count);
		synchronized (this) {
			this.packets = count;
		}
	}

	/**
     * Return packets count.
     *
     * @return Packets in the flow
     */
	public final long getPacketsCount() {
		synchronized (this) {
			return this.packets;
		}
	}

	/**
     * Set count of octets.
     *
     * @param count count of octets.
     * @see #MAX_BYTES
     */
	public void setBytesCount(long count) {
		if (count < 0 || count > MAX_BYTES) { throw new IllegalArgumentException("count: " + count); }
		synchronized (this) {
			this.octets = count;
		}
	}

	/**
     * Return octets count.
     *
     * @return octets count.
     */
	public long getBytesCount() {
		synchronized (this) {
			return this.octets;
		}
	}

	/**
     * Set First packet uptime
     *
     * @param uptime SysUptime at start of flow
     * @see #MAX_UPTIME
     */
	public final void setFirst(long uptime) {
		if (uptime < 0 || uptime > MAX_UPTIME) { throw new IllegalArgumentException("uptime: " + uptime); }
		synchronized (this) {
			this.first = uptime;
		}
	}

	/**
     * Return First packet uptime.
     *
     * @return SysUptime at start of flow.
     */
	public final long getFirst() {
		synchronized (this) {
			return this.first;
		}
	}

	/**
     * Set Last packet uptime.
     *
     * @param uptime SysUptime at the time the last packet of the flow was received
     * @see #MAX_UPTIME
     */
	public final void setLast(long uptime) {
		if (uptime < 0 || uptime > MAX_UPTIME) { throw new IllegalArgumentException("uptime: " + uptime); }
		synchronized (this) {
			this.last = uptime;
		}
	}

	/**
     * Return Last packet uptime.
     *
     * @return SysUptime at the time the last packet of the flow was received
     */
	public final long getLast() {
		synchronized (this) {
			return this.last;
		}
	}

	/**
     * Convert flow to traffic element.
     *
     * @param routerBootTime time of flow server boot (useed in uptime calculations)
     * @return traffic element from this flow
     */
	public TrafficElement toTrafficElement(long routerBootTime) {
		if (routerBootTime < 0) throw new IllegalArgumentException("startTime: " + routerBootTime);
		TrafficElement el = new TrafficElement();
		el.setTime(routerBootTime + this.last);
		el.setPackets(this.packets > 0 ? Long.valueOf(this.packets) : null);
		el.setBytes(this.octets);
		return el;
	}

	/**
     * Aggregate traffic of other flow if flow is equals. Implementations MUST override this method to test if can be
     * merged
     *
     * @param flow flow to aggregate
     * @return true if can aggregate (flows mutch) or false if can't
     */
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (this.getClass().equals(flow.getClass())) {
			synchronized (this) {
				this.packets += flow.getPacketsCount();
				this.octets += flow.getBytesCount();
				if (flow.getFirst() < this.first) this.first = flow.getFirst();
				if (flow.getLast() > this.last) this.last = flow.getLast();
			}
			return true;
		}
		return false;
	}

	/**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (this.first ^ (this.first >>> 32));
		result = PRIME * result + (int) (this.last ^ (this.last >>> 32));
		result = PRIME * result + (int) (this.octets ^ (this.octets >>> 32));
		result = PRIME * result + (int) (this.packets ^ (this.packets >>> 32));
		return result;
	}

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Flow other = (Flow) obj;
		if (this.first != other.first) return false;
		if (this.last != other.last) return false;
		if (this.octets != other.octets) return false;
		if (this.packets != other.packets) return false;
		return true;
	}
}
