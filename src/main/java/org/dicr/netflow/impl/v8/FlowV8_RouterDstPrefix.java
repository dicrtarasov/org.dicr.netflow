package org.dicr.netflow.impl.v8;

import java.util.*;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;
import org.dicr.util.net.*;

/**
 * Flow version 8. Router Dst Prefix aggregation.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */

public class FlowV8_RouterDstPrefix extends FlowV8 {
	/** Destination IP address prefix */
	private int dstPrefix = 0;

	/** Destination address prefix mask; always set to zero */
	private int dstMask = 0;

	/** Destination autonomous system number, either origin or peer; always set to zero */
	private int dstAs = 0;

	/** SNMP index of output interface */
	private int ifOutput = 0;

	/**
     * Constructor
     */
	public FlowV8_RouterDstPrefix() {
		super();
	}

	/**
     * Constructor
     *
	 * @param element traffic element to initialize data from
     */
	public FlowV8_RouterDstPrefix(TrafficElement element) {
		super(element);
		if (element.getDst() != null) this.dstPrefix = element.getDst().toInteger();
		if (element.getDstMask() != null) this.dstMask = element.getDstMask().toInteger();
		if (element.getDstAs() != null) this.dstAs = element.getDstAs().intValue();
		if (element.getDstIf() != null) this.ifOutput = element.getDstIf().intValue();
	}

	/**
     * @see org.dicr.netflow.packet.Flow#getFlowType()
     */
	@Override
	public AggregationScheme getAggregationScheme() {
		return AggregationScheme.DST_PREFIX;
	}

	/**
     * Return DstPrefix
     *
     * @return Destination IP address prefix
     */
	public int getDstPrefix() {
		return this.dstPrefix;
	}

	/**
     * Set DstPrefix
     *
     * @param prefix Destination IP address prefix
     */
	public void setDstPrefix(int prefix) {
		this.dstPrefix = prefix;
	}

	/**
     * Return DstMask
     *
     * @return Destination address prefix mask; always set to zero
     */
	public int getDstMask() {
		return this.dstMask;
	}

	/**
     * Set DstMask
     *
     * @param mask Destination address prefix mask; always set to zero
     */
	public void setDstMask(int mask) {
		try {
			Mask.check(mask);
			this.dstMask = mask;
		} catch (IncorrectAddressException ex) {
			throw new IllegalArgumentException("dstMask: " + mask);
		}
	}

	/**
     * Return DstAS
     *
     * @return Destination autonomous system number, either origin or peer; always set to zero
     */
	public int getDstAs() {
		return this.dstAs;
	}

	/**
     * Set DstAS
     *
     * @param dst_as Destination autonomous system number, either origin or peer; always set to zero
     */
	public void setDstAs(int dst_as) {
		if (dst_as < 0 || dst_as > AS_VALUE_MAX) throw new IllegalArgumentException("dstAs: " + dst_as);
		this.dstAs = dst_as;
	}

	/**
     * Return output interface
     *
     * @return SNMP index of output interface
     */
	public int getIfOutput() {
		return this.ifOutput;
	}

	/**
     * Set output interface
     *
     * @param out_int SNMP index of output interface
     */
	public void setIfOutput(int out_int) {
		if (out_int < 0 || out_int > IFINDEX_VALUE_MAX) throw new IllegalArgumentException("ifOutput: " + out_int);
		this.ifOutput = out_int;
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
		el.setDst(this.dstPrefix != 0 ? new IP(this.dstPrefix) : null);
		try {
			el.setDstMask(this.dstMask != 0 ? new Mask(this.dstMask) : null);
		} catch (IncorrectAddressException ex) {
			// never must be here
			throw new Error(ex);
		}
		el.setDstAs(this.dstAs != 0 ? Integer.valueOf(this.dstAs) : null);
		el.setDstIf(this.ifOutput != 0 ? Integer.valueOf(this.ifOutput) : null);
		return el;
	}

	/**
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV8_RouterDstPrefix)) return false;
		FlowV8_RouterDstPrefix flow8 = (FlowV8_RouterDstPrefix) flow;
		synchronized (this) {
			if (this.dstAs != flow8.dstAs) return false;
			if (this.dstMask != flow8.dstMask) return false;
			if (this.dstPrefix != flow8.dstPrefix) return false;
			if (this.ifOutput != flow8.ifOutput) return false;
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
		if (this.dstPrefix != 0) fields.add("dst=" + IP.toString(this.dstPrefix));
		if (this.dstMask != 0) fields.add("dstMask=" + IP.toString(this.dstMask));
		if (this.dstAs != 0) fields.add("dstAs=" + this.dstAs);
		fields.add("ifOutput=" + this.ifOutput);
		if (this.getPacketsCount() > 0) fields.add("packets=" + this.getPacketsCount());
		if (this.getBytesCount() > 0) fields.add("bytes=" + this.getBytesCount());
		fields.add("flows=" + this.getFlowsAggregated());
		if (this.getFirst() != 0) fields.add("first=" + this.getFirst());
		if (this.getLast() != 0) fields.add("last=" + this.getLast());
		StringBuilder sb = new StringBuilder(FlowV8_RouterDstPrefix.class.getSimpleName() + "{");
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
		result = PRIME * result + this.dstAs;
		result = PRIME * result + this.dstMask;
		result = PRIME * result + this.dstPrefix;
		result = PRIME * result + this.ifOutput;
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
		final FlowV8_RouterDstPrefix other = (FlowV8_RouterDstPrefix) obj;
		if (this.dstAs != other.dstAs) return false;
		if (this.dstMask != other.dstMask) return false;
		if (this.dstPrefix != other.dstPrefix) return false;
		if (this.ifOutput != other.ifOutput) return false;
		return true;
	}
}
