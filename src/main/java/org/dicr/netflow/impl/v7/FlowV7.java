/**
 * FlowV7.java
 */
package org.dicr.netflow.impl.v7;

import java.util.*;

import org.dicr.netflow.impl.v5.*;
import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;
import org.dicr.util.net.*;

/**
 * Flow Version 7
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060122
 */
public class FlowV7 extends FlowV5 {
	/** Flags indicating, among other things, what flow fields are invalid. */
	private byte flags1 = 0;

	/** Flags indicating, among other things, what flows are invalid. */
	private short flags2 = 0;

	/**
     * IP address of the router that is bypassed by the Catalyst 5000 series switch.<BR>
     * This is the same address the router uses when it sends NetFlow export packets. This IP address is propagated to
     * all switches bypassing the router through the FCP protocol.
     */
	private int routerSc = 0;

	/**
     * Constructor
     */
	public FlowV7() {
		super();
	}

	/**
     * Constructor
     *
	 * @param element traffic element to initialize data from
     */
	public FlowV7(TrafficElement element) {
		super(element);
		if (element.getRouter() != null) this.routerSc = element.getRouter().toInteger();
	}

	/**
     * @see org.dicr.netflow.impl.v5.FlowV5#getFlowType()
     */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV7.INSTANCE;
	}

	/**
     * Set Flags1
     *
     * @param flags Flags indicating, among other things, what flow fields are invalid.
     */
	public void setFlags1(byte flags) {
		this.flags1 = flags;
	}

	/**
     * Return Flags1
     *
     * @return Flags indicating, among other things, what flow fields are invalid.
     */
	public byte getFlags1() {
		return this.flags1;
	}

	/**
     * Set flags2
     *
     * @param flags Flags indicating, among other things, what flows are invalid.
     */
	public void setFlags2(short flags) {
		this.flags2 = flags;
	}

	/**
     * Return Flags2
     *
     * @return Flags indicating, among other things, what flows are invalid.
     */
	public short getFlags2() {
		return this.flags2;
	}

	/**
     * Set RouterSc
     *
     * @param ip address of the router uses when it sends NetFlow export packets
     */
	public void setRouterSc(int ip) {
		this.routerSc = ip;
	}

	/**
     * Return RouterSc
     *
     * @return ip address of the router uses when it sends NetFlow export packets
     */
	public int getRouterSc() {
		return this.routerSc;
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
		el.setRouter(this.routerSc != 0 ? new IP(this.routerSc) : null);
		return el;
	}

	/**
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV7)) return false;
		FlowV7 flow7 = (FlowV7) flow;
		synchronized (this) {
			if (this.flags1 != flow7.flags1) return false;
			if (this.flags2 != flow7.flags2) return false;
			if (this.routerSc != flow7.routerSc) return false;
			return super.merge(flow7);
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
		if (this.routerSc != 0) fields.add("routerSc=" + IP.toString(this.routerSc));
		if (this.getSrcAddress() != 0) fields.add("src=" + IP.toString(this.getSrcAddress()));
		if (this.getDstAddress() != 0) fields.add("dst=" + IP.toString(this.getDstAddress()));
		if (this.getSrcMask() != 0) fields.add("srcMask=" + IP.toString(this.getSrcMask()));
		if (this.getDstMask() != 0) fields.add("dstMask=" + IP.toString(this.getDstMask()));
		if (this.getSrcAs() != 0) fields.add("srcAs=" + this.getSrcAs());
		if (this.getDstAs() != 0) fields.add("dstAs=" + this.getDstAs());
		if (this.getNextHop() != 0) fields.add("nextHop=" + IP.toString(this.getNextHop()));
		fields.add("ifInput=" + this.getInInterface());
		fields.add("ifOutput=" + this.getOutInterface());
		if (this.getSrcPort() != 0) fields.add("srcPort=" + this.getSrcPort());
		if (this.getDstPort() != 0) fields.add("dstPort=" + this.getDstPort());
		if (this.getProto() != 0) fields.add("proto=" + this.getProto());
		if (this.getTcpFlags() != 0) fields.add(String.format("tcpFlags=%02X", Byte.valueOf(this.getTcpFlags())));
		if (this.getTos() != 0) fields.add(String.format("tos=%02X", Byte.valueOf(this.getTos())));
		if (this.getPacketsCount() > 0) fields.add("packets=" + this.getPacketsCount());
		if (this.getBytesCount() > 0) fields.add("bytes=" + this.getBytesCount());
		if (this.getFirst() != 0) fields.add("first=" + this.getFirst());
		if (this.getLast() != 0) fields.add("last=" + this.getLast());
		if (this.flags1 != 0) fields.add("flags1=" + this.flags1);
		if (this.flags2 != 0) fields.add("flags2=" + this.flags2);
		StringBuilder sb = new StringBuilder(FlowV7.class.getSimpleName() + "{");
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
		result = PRIME * result + this.flags2;
		result = PRIME * result + this.routerSc;
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
		final FlowV7 other = (FlowV7) obj;
		if (this.flags1 != other.flags1) return false;
		if (this.flags2 != other.flags2) return false;
		if (this.routerSc != other.routerSc) return false;
		return true;
	}
}
