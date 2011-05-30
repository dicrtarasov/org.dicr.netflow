package org.dicr.netflow.impl.v8;

import java.util.*;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;
import org.dicr.util.net.*;

/**
 * Flow version 8. RouterSrcPrefix aggregation.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class FlowV8_RouterSrcPrefix extends FlowV8 {
	/** Source IP address prefix */
	private int srcPrefix = 0;

	/** Source address prefix mask; always set to zero */
	private int srcMask = 0;

	/** Source autonomous system number, either origin or peer; always set to zero */
	private int srcAs = 0;

	/** SNMP index of input interface; always set to zero */
	private int ifInput = 0;

	/**
     * Constructor
     */
	public FlowV8_RouterSrcPrefix() {
		super();
	}

	/**
     * Constructor
     *
     * @param element traffic element to initiualize data fields from
     */
	public FlowV8_RouterSrcPrefix(TrafficElement element) {
		super(element);
		if (element.getSrc() != null) this.srcPrefix = element.getSrc().toInteger();
		if (element.getSrcMask() != null) this.srcMask = element.getSrcMask().toInteger();
		if (element.getSrcAs() != null) this.srcAs = element.getSrcAs().intValue();
		if (element.getSrcIf() != null) this.ifInput = element.getSrcIf().intValue();
	}

	/**
     * @see org.dicr.netflow.packet.Flow#getFlowType()
     */
	@Override
	public AggregationScheme getAggregationScheme() {
		return AggregationScheme.SRC_PREFIX;
	}

	/**
     * Return SrcPrefix.
     *
     * @return Source IP address prefix
     */
	public int getSrcPrefix() {
		return this.srcPrefix;
	}

	/**
     * Set SrcPrefix
     *
     * @param prefix Source IP address prefix
     */
	public void setSrcPrefix(int prefix) {
		this.srcPrefix = prefix;
	}

	/**
     * Return SrcMask
     *
     * @return Source address prefix mask; always set to zero
     */
	public int getSrcMask() {
		return this.srcMask;
	}

	/**
     * Set SrcMask
     *
     * @param mask Source address prefix mask; always set to zero
     */
	public void setSrcMask(int mask) {
		try {
			Mask.check(mask);
			this.srcMask = mask;
		} catch (IncorrectAddressException ex) {
			throw new IllegalArgumentException("srcMask: " + mask);
		}
	}

	/**
     * Return SrcAS
     *
     * @return Source autonomous system number, either origin or peer; always set to zero
     */
	public int getSrcAs() {
		return this.srcAs;
	}

	/**
     * Set SrcAS
     *
     * @param as Source autonomous system number, either origin or peer; always set to zero
     * @see #AS_VALUE_MAX
     */
	public void setSrcAs(int as) {
		if (as < 0 || as > AS_VALUE_MAX) throw new IllegalArgumentException("srcAs: " + as);
		this.srcAs = as;
	}

	/**
     * Return input interface
     *
     * @return SNMP index of input interface; always set to zero
     */
	public int getIfInput() {
		return this.ifInput;
	}

	/**
     * Set input interface
     *
     * @param in_interface SNMP index of input interface; always set to zero
     * @see #IFINDEX_VALUE_MAX
     */
	public void setIfInput(int in_interface) {
		if (in_interface < 0 || in_interface > IFINDEX_VALUE_MAX) throw new IllegalArgumentException("ifInput: "
				+ in_interface);
		this.ifInput = in_interface;
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
		el.setSrc(this.srcPrefix != 0 ? new IP(this.srcPrefix) : null);
		try {
			el.setSrcMask(this.srcMask != 0 ? new Mask(this.srcMask) : null);
		} catch (IncorrectAddressException ex) {
			// never must be here
			throw new Error(ex);
		}
		el.setSrcAs(this.srcAs != 0 ? Integer.valueOf(this.srcAs) : null);
		el.setSrcIf(Integer.valueOf(this.ifInput));
		return el;
	}

	/**
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV8_RouterSrcPrefix)) return false;
		FlowV8_RouterSrcPrefix flow8 = (FlowV8_RouterSrcPrefix) flow;
		synchronized (this) {
			if (this.ifInput != flow8.ifInput) return false;
			if (this.srcAs != flow8.srcAs) return false;
			if (this.srcMask != flow8.srcMask) return false;
			if (this.srcPrefix != flow8.srcPrefix) return false;
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
		if (this.srcPrefix != 0) fields.add("src=" + IP.toString(this.srcPrefix));
		if (this.srcMask != 0) fields.add("srcMask=" + IP.toString(this.srcMask));
		if (this.srcAs != 0) fields.add("srcAs=" + this.srcAs);
		fields.add("ifInput=" + this.ifInput);
		if (this.getPacketsCount() > 0) fields.add("packets=" + this.getPacketsCount());
		if (this.getBytesCount() > 0) fields.add("bytes=" + this.getBytesCount());
		fields.add("flows=" + this.getFlowsAggregated());
		if (this.getFirst() != 0) fields.add("first=" + this.getFirst());
		if (this.getLast() != 0) fields.add("last=" + this.getLast());
		StringBuilder sb = new StringBuilder(FlowV8_RouterSrcPrefix.class.getSimpleName() + "{");
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
		result = PRIME * result + this.ifInput;
		result = PRIME * result + this.srcAs;
		result = PRIME * result + this.srcMask;
		result = PRIME * result + this.srcPrefix;
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
		final FlowV8_RouterSrcPrefix other = (FlowV8_RouterSrcPrefix) obj;
		if (this.ifInput != other.ifInput) return false;
		if (this.srcAs != other.srcAs) return false;
		if (this.srcMask != other.srcMask) return false;
		if (this.srcPrefix != other.srcPrefix) return false;
		return true;
	}
}
