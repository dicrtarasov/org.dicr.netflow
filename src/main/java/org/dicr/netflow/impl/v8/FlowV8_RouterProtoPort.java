package org.dicr.netflow.impl.v8;

import java.util.*;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * Flow version 8. RouterProtoPort aggregation.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */

public class FlowV8_RouterProtoPort extends FlowV8 {
	/** Maximum value for port */
	public static final int PORT_VALUE_MAX = 0x0FFFF;

	/** Maximum value for protocol */
	public static final int PROTO_MAX_VALUE = 0x0FF;

	/**
     * IP protocol type (for example, TCP = 6; UDP = 17); set to zero if flow mask is destination-only or
     * source-destination TODO: MAX VALUE not Integer
     */
	private int proto = 0;

	/** TCP/UDP source port number; set to zero if flow mask is destination-only or source-destination */
	private int srcPort = 0;

	/** TCP/UDP destination port number; set to zero if flow mask is destination-only or source-destination */
	private int dstPort = 0;

	/**
     * Constructor
     */
	public FlowV8_RouterProtoPort() {
		super();
	}

	/**
     * Constructor
     *
     * @param element traffic element to initialize data from
     */
	public FlowV8_RouterProtoPort(TrafficElement element) {
		super(element);
		if (element.getProto() != null) this.proto = element.getProto().intValue();
		if (element.getSrcPort() != null) this.srcPort = element.getSrcPort().intValue();
		if (element.getDstPort() != null) this.dstPort = element.getDstPort().intValue();
	}

	/**
     * @see org.dicr.netflow.packet.Flow#getFlowType()
     */
	@Override
	public AggregationScheme getAggregationScheme() {
		return AggregationScheme.PROTO_PORT;
	}

	/**
     * Return Proto.
     *
     * @return IP protocol type (for example, TCP = 6; UDP = 17); set to zero if flow mask is destination-only or
     *         source-destination
     */
	public int getProto() {
		return this.proto;
	}

	/**
     * Set protocol
     *
     * @param aProto IP protocol type (for example, TCP = 6; UDP = 17); set to zero if flow mask is destination-only or
     *            source-destination
     * @see #PROTO_MAX_VALUE
     */
	public void setProto(int aProto) {
		if (aProto < 0 || aProto > PROTO_MAX_VALUE) throw new IllegalArgumentException("proto: " + aProto);
		this.proto = aProto;
	}

	/**
     * Return source port
     *
     * @return TCP/UDP source port number; set to zero if flow mask is destination-only or source-destination
     */
	public int getSrcPort() {
		return this.srcPort;
	}

	/**
     * Set source port
     *
     * @param port TCP/UDP source port number; set to zero if flow mask is destination-only or source-destination
     * @see #PORT_VALUE_MAX
     */
	public void setSrcPort(int port) {
		if (port < 0 || port > PORT_VALUE_MAX) throw new IllegalArgumentException("srcPort: " + port);
		this.srcPort = port;
	}

	/**
     * Return destination port
     *
     * @return TCP/UDP destination port number; set to zero if flow mask is destination-only or source-destination
     */
	public int getDstPort() {
		return this.dstPort;
	}

	/**
     * Set destination port
     *
     * @param port TCP/UDP destination port number; set to zero if flow mask is destination-only or source-destination
     * @see #PORT_VALUE_MAX
     */
	public void setDstPort(int port) {
		if (port < 0 || port > PORT_VALUE_MAX) throw new IllegalArgumentException("dstPort: " + port);
		this.dstPort = port;
	}

	/**
     * Convert flow to traffic element.
     *
     * @param routerBootTime time of flow server boot (useed in uptime calculations)
     * @return traffic element from this flow
     */
	@Override
	public TrafficElement toTrafficElement(long routerBootTime) {
		TrafficElement el = super.toTrafficElement(routerBootTime);
		el.setProto(Integer.valueOf(this.proto));
		el.setSrcPort(Integer.valueOf(this.srcPort));
		el.setDstPort(Integer.valueOf(this.dstPort));
		return el;
	}

	/**
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV8_RouterProtoPort)) return false;
		FlowV8_RouterProtoPort flow8 = (FlowV8_RouterProtoPort) flow;
		synchronized (this) {
			if (this.dstPort != flow8.dstPort) return false;
			if (this.proto != flow8.proto) return false;
			if (this.srcPort != flow8.srcPort) return false;
			return super.merge(flow8);
		}
	}

	/**
     * Convert to string
     *
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		Collection<String> fields = new ArrayList<String>();
		if (this.proto != 0) fields.add("proto=" + this.proto);
		if (this.srcPort != 0) fields.add("srcPort=" + this.srcPort);
		if (this.dstPort != 0) fields.add("dstPort=" + this.dstPort);
		if (this.getPacketsCount() > 0) fields.add("packets=" + this.getPacketsCount());
		if (this.getBytesCount() > 0) fields.add("bytes=" + this.getBytesCount());
		fields.add("flows=" + this.getFlowsAggregated());
		if (this.getFirst() != 0) fields.add("first=" + this.getFirst());
		if (this.getLast() != 0) fields.add("last=" + this.getLast());
		StringBuilder sb = new StringBuilder(FlowV8_RouterProtoPort.class.getSimpleName() + "{");
		Iterator<String> iterator = fields.iterator();
		while (true) {
			sb.append(iterator.next());
			if (iterator.hasNext()) sb.append(",");
			else break;
		}
		sb.append("}");
		return sb.toString();
	}

	/**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + this.dstPort;
		result = PRIME * result + this.proto;
		result = PRIME * result + this.srcPort;
		return result;
	}

	/**
     * @see java.lang.Object#equals(java.lang.Object)
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		final FlowV8_RouterProtoPort other = (FlowV8_RouterProtoPort) obj;
		if (this.dstPort != other.dstPort) return false;
		if (this.proto != other.proto) return false;
		if (this.srcPort != other.srcPort) return false;
		return true;
	}
}
