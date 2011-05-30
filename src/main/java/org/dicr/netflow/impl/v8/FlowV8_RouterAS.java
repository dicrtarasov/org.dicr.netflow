package org.dicr.netflow.impl.v8;

import java.util.*;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * Flow version 8. Router AS aggregation.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class FlowV8_RouterAS extends FlowV8 {
	/** Source autonomous system number, either origin or peer; always set to zero */
	private int srcAs = 0;

	/** Destination autonomous system number, either origin or peer; always set to zero */
	private int dstAs = 0;

	/** SNMP index of input interface; always set to zero */
	private int ifInput = 0;

	/** SNMP index of output interface */
	private int ifOutput = 0;

	/**
     * Constructor
     */
	public FlowV8_RouterAS() {
		super();
	}

	/**
     * Constructor
     *
	 * @param element traffic element to initialize data from
     */
	public FlowV8_RouterAS(TrafficElement element) {
		super(element);
		if (element.getSrcAs() != null) this.srcAs = element.getSrcAs().intValue();
		if (element.getDstAs() != null) this.dstAs = element.getDstAs().intValue();
		if (element.getSrcIf() != null) this.ifInput = element.getSrcIf().intValue();
		if (element.getDstIf() != null) this.ifOutput = element.getDstIf().intValue();
	}

	/**
     * @see org.dicr.netflow.packet.Flow#getFlowType()
     */
	@Override
	public AggregationScheme getAggregationScheme() {
		return AggregationScheme.AS;
	}

	/**
     * Return SrcAs
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
     */
	public void setSrcAs(int as) {
		if (as < 0 || as > AS_VALUE_MAX) throw new IllegalArgumentException("srcAs: " + as);
		this.srcAs = as;
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
     * @param as Destination autonomous system number, either origin or peer; always set to zero
     */
	public void setDstAs(int as) {
		if (as < 0 || as > AS_VALUE_MAX) throw new IllegalArgumentException("dstAs: " + as);
		this.dstAs = as;
	}

	/**
     * Get input interface
     *
     * @return SNMP index of input interface; always set to zero
     */
	public int getInputInterface() {
		return this.ifInput;
	}

	/**
     * Set input interface
     *
     * @param input SNMP index of input interface; always set to zero
     */
	public void setInputInterface(int input) {
		if (input < 0 || input > IFINDEX_VALUE_MAX) throw new IllegalArgumentException("ifInput: " + input);
		this.ifInput = input;
	}

	/**
     * Return output interface
     *
     * @return SNMP index of output interface
     */
	public int getOutputInterface() {
		return this.ifOutput;
	}

	/**
     * Set output interface
     *
     * @param output SNMP index of output interface
     */
	public void setOutputInterface(int output) {
		if (output < 0 || output > IFINDEX_VALUE_MAX) throw new IllegalArgumentException("ifOutput: " + output);
		this.ifOutput = output;
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
		el.setSrcIf(Integer.valueOf(this.ifInput));
		el.setDstIf(Integer.valueOf(this.ifOutput));
		return el;
	}

	/**
     * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
     */
	@Override
	public boolean merge(Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV8_RouterAS)) return false;
		FlowV8_RouterAS flow8 = (FlowV8_RouterAS) flow;
		synchronized (this) {
			if (this.dstAs != flow8.dstAs) return false;
			if (this.ifInput != flow8.ifInput) return false;
			if (this.ifOutput != flow8.ifOutput) return false;
			if (this.srcAs != flow8.srcAs) return false;
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
		if (this.srcAs != 0) fields.add("srcAs=" + this.srcAs);
		if (this.dstAs != 0) fields.add("dstAs=" + this.dstAs);
		fields.add("ifInput=" + this.ifInput);
		fields.add("ifOutput=" + this.ifOutput);
		if (this.getPacketsCount() > 0) fields.add("packets=" + this.getPacketsCount());
		if (this.getBytesCount() > 0) fields.add("bytes=" + this.getBytesCount());
		fields.add("flows=" + this.getFlowsAggregated());
		if (this.getFirst() != 0) fields.add("first=" + this.getFirst());
		if (this.getLast() != 0) fields.add("last=" + this.getLast());
		StringBuilder sb = new StringBuilder(FlowV8_RouterAS.class.getSimpleName() + "{");
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
		result = PRIME * result + this.ifInput;
		result = PRIME * result + this.ifOutput;
		result = PRIME * result + this.srcAs;
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
		final FlowV8_RouterAS other = (FlowV8_RouterAS) obj;
		if (this.dstAs != other.dstAs) return false;
		if (this.ifInput != other.ifInput) return false;
		if (this.ifOutput != other.ifOutput) return false;
		if (this.srcAs != other.srcAs) return false;
		return true;
	}
}
