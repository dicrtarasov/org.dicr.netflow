/**
 * FlowV1.java
 */
package org.dicr.netflow.impl.v1;

import java.util.*;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;
import org.dicr.util.net.*;

/**
 * Flow V1
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 030505
 */
public class FlowV1 extends Flow {
	/** Maximum value of interface index */
	public static final int IFINDEX_VALUE_MAX = 0x0FFFF;

	/** Maximum value for port */
	public static final int PORT_VALUE_MAX = 0x0FFFF;

	/** Maximum value for protocol */
	public static final int PROTO_MAX_VALUE = 0x0FF;

	/** Source IP address */
	private int srcAddr = 0;

	/** Destination IP address */
	private int dstAddr = 0;

	/** IP address of next hop router */
	private int nextHop = 0;

	/** SNMP index of input interface */
	private int ifInput = 0;

	/** SNMP index of output interface */
	private int ifOutput = 0;

	/** TCP/UDP source port number or equivalent */
	private int srcPort = 0;

	/** TCP/UDP destination port number or equivalent */
	private int dstPort = 0;

	/** IP protocol type (for example, TCP = 6; UDP = 17) */
	private int proto = 0;

	/** IP type of service (ToS) */
	private byte tos = 0;

	/** Cumulative OR of TCP flags */
	private byte tcpFlags = 0;

	/**
     * Constructor.
     */
	public FlowV1() {
		super();
	}

	/**
     * Constructor
     *
     * @param element traffic element to initialize data from
     */
	public FlowV1(TrafficElement element) {
		super(element);
		if (element.getHop() != null) this.nextHop = element.getHop().toInteger();
		if (element.getSrc() != null) this.srcAddr = element.getSrc().toInteger();
		if (element.getDst() != null) this.dstAddr = element.getDst().toInteger();
		if (element.getSrcIf() != null) this.ifInput = element.getSrcIf().intValue();
		if (element.getDstIf() != null) this.ifOutput = element.getDstIf().intValue();
		if (element.getSrcPort() != null) this.srcPort = element.getSrcPort().intValue();
		if (element.getDstPort() != null) this.dstPort = element.getDstPort().intValue();
		if (element.getProto() != null) this.proto = element.getProto().intValue();
		if (element.getTcpFlags() != null) this.tcpFlags = element.getTcpFlags().byteValue();
		if (element.getTos() != null) this.tos = element.getTos().byteValue();
	}

	/**
     * @see org.dicr.netflow.packet.Flow#getFlowType()
     */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV1.INSTANCE;
	}

	/**
     * Set source address
     *
     * @param ip source address
     */
	public final void setSrcAddress(int ip) {
		this.srcAddr = ip;
	}

	/**
     * Return source address
     *
     * @return source address
     */
	public final int getSrcAddress() {
		return this.srcAddr;
	}

	/**
     * Set destination address
     *
     * @param ip destination address
     */
	public final void setDstAddress(int ip) {
		this.dstAddr = ip;
	}

	/**
     * Return destination address
     *
     * @return destination address
     */
	public final int getDstAddress() {
		return this.dstAddr;
	}

	/**
     * Set next hop
     *
     * @param ip next hop address
     */
	public final void setNextHop(int ip) {
		this.nextHop = ip;
	}

	/**
     * Return next hop
     *
     * @return next hop address
     */
	public final int getNextHop() {
		return this.nextHop;
	}

	/**
     * Set input interface
     *
     * @param number input interface index
     * @see #IFINDEX_VALUE_MAX
     */
	public final void setInInterface(int number) {
		if (number < 0 || number > IFINDEX_VALUE_MAX) throw new IllegalArgumentException("ifnum: " + number);
		this.ifInput = number;
	}

	/**
     * Return input interface
     *
     * @return input interface index
     */
	public final int getInInterface() {
		return this.ifInput;
	}

	/**
     * Set output interface
     *
     * @param number output interface index
     */
	public final void setOutInterface(int number) {
		if (number < 0 || number > IFINDEX_VALUE_MAX) throw new IllegalArgumentException("ifnum: " + number);
		this.ifOutput = number;
	}

	/**
     * Return iutput interface
     *
     * @return output interface index
     */
	public final int getOutInterface() {
		return this.ifOutput;
	}

	/**
     * Set source port
     *
     * @param port source port
     * @see #PORT_VALUE_MAX
     */
	public final void setSrcPort(int port) {
		if (port < 0 || port > PORT_VALUE_MAX) throw new IllegalArgumentException("port: " + port);
		this.srcPort = port;
	}

	/**
     * Return source port
     *
     * @return source port number
     */
	public final int getSrcPort() {
		return this.srcPort;
	}

	/**
     * Set destination port
     *
     * @param port destination port number
     * @see #PORT_VALUE_MAX
     */
	public final void setDstPort(int port) {
		if (port < 0 || port > PORT_VALUE_MAX) throw new IllegalArgumentException("port: " + port);
		this.dstPort = port;
	}

	/**
     * Return destination port
     *
     * @return destination port number
     */
	public final int getDstPort() {
		return this.dstPort;
	}

	/**
     * Set protocol
     *
     * @param number protocol number
     * @see #PROTO_MAX_VALUE
     */
	public final void setProtocol(int number) {
		if (number < 0 || number > PROTO_MAX_VALUE) throw new IllegalArgumentException("proto: " + number);
		this.proto = number;
	}

	/**
     * Return protocol
     *
     * @return protocol number
     */
	public final int getProto() {
		return this.proto;
	}

	/**
     * Set TOS flags
     *
     * @param value TOS flags value
     */
	public final void setTos(byte value) {
		this.tos = value;
	}

	/**
     * Return TOS flags
     *
     * @return flags value
     */
	public final byte getTos() {
		return this.tos;
	}

	/**
     * Set TCP flags
     *
     * @param flags tcp flags value
     */
	public final void setTcpFlags(byte flags) {
		this.tcpFlags = flags;
	}

	/**
     * Return TCP flags
     *
     * @return flags value
     */
	public final byte getTcpFlags() {
		return this.tcpFlags;
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
		el.setSrc(this.srcAddr != 0 ? new IP(this.srcAddr) : null);
		el.setDst(this.dstAddr != 0 ? new IP(this.dstAddr) : null);
		el.setHop(this.nextHop != 0 ? new IP(this.nextHop) : null);
		el.setSrcIf(Integer.valueOf(this.ifInput));
		el.setDstIf(Integer.valueOf(this.ifOutput));
		el.setSrcPort(Integer.valueOf(this.srcPort));
		el.setDstPort(Integer.valueOf(this.dstPort));
		el.setProto(Integer.valueOf(this.proto));
		el.setTcpFlags(Byte.valueOf(this.tcpFlags));
		el.setTos(Byte.valueOf(this.tos));
		return el;
	}

	/**
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV1)) return false;
		FlowV1 flow1 = (FlowV1) flow;
		synchronized (this) {
			if (this.dstAddr != flow1.dstAddr) return false;
			if (this.dstPort != flow1.dstPort) return false;
			if (this.ifInput != flow1.ifInput) return false;
			if (this.ifOutput != flow1.ifOutput) return false;
			if (this.nextHop != flow1.nextHop) return false;
			if (this.proto != flow1.proto) return false;
			if (this.srcAddr != flow1.srcAddr) return false;
			if (this.srcPort != flow1.srcPort) return false;
			if (this.tcpFlags != flow1.tcpFlags) return false;
			if (this.tos != flow1.tos) return false;
			return super.merge(flow1);
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
		if (this.srcAddr != 0) fields.add("src=" + IP.toString(this.srcAddr));
		if (this.dstAddr != 0) fields.add("dst=" + IP.toString(this.dstAddr));
		if (this.nextHop != 0) fields.add("nextHop=" + IP.toString(this.nextHop));
		fields.add("ifInput=" + this.ifInput);
		fields.add("ifOutput=" + this.ifOutput);
		if (this.srcPort != 0) fields.add("srcPort=" + this.srcPort);
		if (this.dstPort != 0) fields.add("dstPort=" + this.dstPort);
		if (this.proto != 0) fields.add("proto=" + this.proto);
		if (this.tcpFlags != 0) fields.add(String.format("tcpFlags=%02X", Byte.valueOf(this.tcpFlags)));
		if (this.tos != 0) fields.add(String.format("tos=%02X", Byte.valueOf(this.tos)));
		if (this.getPacketsCount() > 0) fields.add("packets=" + this.getPacketsCount());
		if (this.getBytesCount() > 0) fields.add("bytes=" + this.getBytesCount());
		if (this.getFirst() != 0) fields.add("first=" + this.getFirst());
		if (this.getLast() != 0) fields.add("last=" + this.getLast());
		StringBuilder sb = new StringBuilder(FlowV1.class.getSimpleName() + "{");
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
		result = PRIME * result + this.dstAddr;
		result = PRIME * result + this.dstPort;
		result = PRIME * result + this.ifInput;
		result = PRIME * result + this.ifOutput;
		result = PRIME * result + this.nextHop;
		result = PRIME * result + this.proto;
		result = PRIME * result + this.srcAddr;
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
		final FlowV1 other = (FlowV1) obj;
		if (this.dstAddr != other.dstAddr) return false;
		if (this.dstPort != other.dstPort) return false;
		if (this.ifInput != other.ifInput) return false;
		if (this.ifOutput != other.ifOutput) return false;
		if (this.nextHop != other.nextHop) return false;
		if (this.proto != other.proto) return false;
		if (this.srcAddr != other.srcAddr) return false;
		if (this.srcPort != other.srcPort) return false;
		if (this.tcpFlags != other.tcpFlags) return false;
		if (this.tos != other.tos) return false;
		return true;
	}
}
