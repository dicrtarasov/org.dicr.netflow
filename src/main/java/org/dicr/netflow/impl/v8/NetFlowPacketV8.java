package org.dicr.netflow.impl.v8;

import org.dicr.netflow.impl.v5.*;
import org.dicr.netflow.packet.*;

/**
 * NetFlow Packet version 8.
 * 
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class NetFlowPacketV8 extends NetFlowPacketV5 {
	/** Aggregation scheme */
	private AggregationScheme aggregationScheme = null;

	/** Maximum value of AggVersion */
	public static final int AGG_VERSION_VALUE_MAX = 0x0FF;

	/** Version of the aggregation export */
	private int aggVersion = 2;

	/**
	 * Constructor. Used by decode method.
	 */
	protected NetFlowPacketV8() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param scheme aggregation scheme
	 */
	public NetFlowPacketV8(final AggregationScheme scheme) {
		super();
		if (scheme == null) throw new IllegalArgumentException("null aggregation scheme");
		this.setAggregationScheme(scheme);
	}

	/**
	 * @see org.dicr.netflow.packet.NetFlowPacket#getFlowType()
	 */
	@Override
	public FlowType getFlowType() {
		return this.aggregationScheme.getFlowType();
	}

	/**
	 * Set flow type
	 * 
	 * @param scheme aggregation scheme to set
	 */
	protected void setAggregationScheme(final AggregationScheme scheme) {
		if (scheme == null) throw new IllegalArgumentException("null aggregation scheme");
		this.aggregationScheme = scheme;
	}

	/**
	 * Return aggregation scheme
	 * 
	 * @return aggregation scheme
	 */
	public AggregationScheme getAggregationScheme() {
		return this.aggregationScheme;
	}

	/**
	 * Return AggVersion
	 * 
	 * @return version of the aggregation export
	 */
	public int getAggregationVersion() {
		return this.aggVersion;
	}

	/**
	 * Set AggVersion.
	 * 
	 * @param agg_version version of the aggregation export
	 * @see #AGG_VERSION_VALUE_MAX
	 */
	public void setAggregationVersion(final int agg_version) {
		this.aggVersion = agg_version;
	}
}
