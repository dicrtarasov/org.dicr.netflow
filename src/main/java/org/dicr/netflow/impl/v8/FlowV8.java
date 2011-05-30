/*
 * FlowV8.java Created on 23 2006 Ц., 21:35 To change this template, choose Tools | Template Manager and open the
 * template in the editor.
 */

package org.dicr.netflow.impl.v8;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * Flow version 8
 * 
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 */
public abstract class FlowV8 extends Flow {
	/** Maximum value of interface index */
	public static final int IFINDEX_VALUE_MAX = 0x0FFFF;

	/** Maximum value of aggregated flows count */
	public static final long AGGREGATED_FLOWS_VALUE_MAX = 0x0FFFFFFFFL;

	/** Maximum value of AS number */
	public static final int AS_VALUE_MAX = 0x0FFFF;

	/** Number of flows aggregated */
	private long flowsAggregated = 0;

	/**
	 * Constructor
	 */
	protected FlowV8() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param element traffic element from which initialize flow data
	 */
	protected FlowV8(final TrafficElement element) {
		super(element);
		this.flowsAggregated = 1;
	}

	/**
	 * Return aggregation scheme
	 * 
	 * @return aggregation scheme
	 */
	public abstract AggregationScheme getAggregationScheme();

	/**
	 * Return flow type
	 * 
	 * @see org.dicr.netflow.packet.Flow#getFlowType()
	 */
	@Override public FlowTypeV8 getFlowType() {
		return this.getAggregationScheme().getFlowType();
	}

	/**
	 * Return aggregated flows count
	 * 
	 * @return number of aggregated flows
	 */
	public long getFlowsAggregated() {
		return this.flowsAggregated;
	}

	/**
	 * Set aggregated flows count
	 * 
	 * @param count count of aggregated flows
	 */
	public void setFlowsAggregated(final long count) {
		if (count < 0 || count > FlowV8.AGGREGATED_FLOWS_VALUE_MAX) throw new IllegalArgumentException("count: "
		        + count);
		this.flowsAggregated = count;
	}

	/**
	 * @see org.dicr.netflow.packet.Flow#merge(org.dicr.netflow.packet.Flow)
	 */
	@Override public boolean merge(final Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (!(flow instanceof FlowV8)) return false;
		final FlowV8 flow8 = (FlowV8) flow;
		boolean aggregated = false;
		synchronized (this) {
			aggregated = super.merge(flow8);
			if (aggregated) this.flowsAggregated++;
		}
		return aggregated;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + (int) (this.flowsAggregated ^ this.flowsAggregated >>> 32);
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (this.getClass() != obj.getClass()) return false;
		final FlowV8 other = (FlowV8) obj;
		if (this.flowsAggregated != other.flowsAggregated) return false;
		return true;
	}
}
