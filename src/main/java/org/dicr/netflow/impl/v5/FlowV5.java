/**
 * FlowV5.java
 */
package org.dicr.netflow.impl.v5;

import java.util.*;

import org.dicr.netflow.impl.v1.*;
import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;
import org.dicr.util.net.*;

/**
 * Flow Version 5
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class FlowV5 extends FlowV1 {
	/** Maximum value of AS number */
	public static final int AS_VALUE_MAX = 0x0FFFF;

	/** Autonomous system number of the source, either origin or peer */
	private int srcAs = 0;

	/** Autonomous system number of the destination, either origin or peer */
	private int dstAs = 0;

	/** Source address prefix mask (mask) */
	private int srcMask = 0;

	/** Destination address prefix mask (mask) */
	private int dstMask = 0;

	/**
     * Constructor
     */
	public FlowV5() {
		super();
	}

	/**
     * Constructor
     *
     * @param element traffic element to initialize data from
     */
	public FlowV5(TrafficElement element) {
		super(element);
		if (element.getSrcMask() != null) this.srcMask = element.getSrcMask().toInteger();
		if (element.getDstMask() != null) this.dstMask = element.getDstMask().toInteger();
		if (element.getSrcAs() != null) this.srcAs = element.getSrcAs().intValue();
		if (element.getDstAs() != null) this.dstAs = element.getDstAs().intValue();
	}

	/**
     * @see org.dicr.netflow.impl.v1.FlowV1#getFlowType()
     */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV5.INSTANCE;
	}

	/**
     * Set SrcAS
     *
     * @param as autonomous system number of the source, either origin or peer
     * @see #AS_VALUE_MAX
     */
	public void setSrcAs(int as) {
		if (as < 0 || as > AS_VALUE_MAX) throw new IllegalArgumentException("as: " + as);
		this.srcAs = as;
	}

	/**
     * Return SrsAS
     *
     * @return autonomous system number of the source, either origin or peer
     */
	public int getSrcAs() {
		return this.srcAs;
	}

	/**
     * Set DstAS
     *
     * @param as autonomous system number of the destination, either origin or peer
     * @see #AS_VALUE_MAX
     */
	public void setDstAs(int as) {
		if (as < 0 || as > AS_VALUE_MAX) throw new IllegalArgumentException("as: " + as);
		this.dstAs = as;
	}

	/**
     * Return DstAS
     *
     * @return autonomous system number of the destination, either origin or peer
     */
	public int getDstAs() {
		return this.dstAs;
	}

	/**
     * Set SrcMask
     *
     * @param mask source address prefix mask bits (mask)
     */
	public void setSrcMask(int mask) {
		try {
			Mask.check(mask);
			this.srcMask = mask;
		} catch (IncorrectAddressException ex) {
			throw new IllegalArgumentException("mask: " + mask);
		}
	}

	/**
     * Return SrcMask
     *
     * @return source address prefix mask bits (mask)
     */
	public int getSrcMask() {
		return this.srcMask;
	}

	/**
     * Set DstMask
     *
     * @param mask destination address prefix mask bits (mask)
     */
	public void setDstMask(int mask) {
		try {
			Mask.check(mask);
			this.dstMask = mask;
		} catch (IncorrectAddressException ex) {
			throw new IllegalArgumentException("mask: " + mask);
		}
	}

	/**
     * Return DstMask
     *
     * @return destination address prefix mask bits (mask)
     */
	public int getDstMask() {
		return this.dstMask;
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
		el.setSrcAs(this.srcAs != 0 ? Integer.valueOf(this.srcAs) : null);
		el.setDstAs(this.dstAs != 0 ? Integer.valueOf(this.dstAs) : null);
		try {
			el.setSrcMask(this.srcMask != 0 ? new Mask(this.srcMask) : null);
			el.setDstMask(this.dstMask != 0 ? new Mask(this.dstMask) : null);
		} catch (IncorrectAddressException ex) {
			// IncorrectAddressException must not be thrown
			throw new Error(ex);
		}
		return el;
	}

	/**
     * Convert to string
     *
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString() {
		Collection<String> fields = new ArrayList<String>();
		if (this.getSrcAddress() != 0) fields.add("src=" + IP.toString(this.getSrcAddress()));
		if (this.getDstAddress() != 0) fields.add("dst=" + IP.toString(this.getDstAddress()));
		if (this.srcMask != 0) fields.add("srcMask=" + IP.toString(this.srcMask));
		if (this.dstMask != 0) fields.add("dstMask=" + IP.toString(this.dstMask));
		if (this.srcAs != 0) fields.add("srcAs=" + this.srcAs);
		if (this.dstAs != 0) fields.add("dstAs=" + this.dstAs);
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
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + "{");
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
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV5)) return false;
		FlowV5 flow5 = (FlowV5) flow;
		synchronized (this) {
			if (this.dstAs != flow5.dstAs) return false;
			if (this.dstMask != flow5.dstMask) return false;
			if (this.srcAs != flow5.srcAs) return false;
			if (this.srcMask != flow5.srcMask) return false;
			return super.merge(flow5);
		}
	}

	/**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + this.dstAs;
		result = PRIME * result + this.dstMask;
		result = PRIME * result + this.srcAs;
		result = PRIME * result + this.srcMask;
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
		final FlowV5 other = (FlowV5) obj;
		if (this.dstAs != other.dstAs) return false;
		if (this.dstMask != other.dstMask) return false;
		if (this.srcAs != other.srcAs) return false;
		if (this.srcMask != other.srcMask) return false;
		return true;
	}
}
