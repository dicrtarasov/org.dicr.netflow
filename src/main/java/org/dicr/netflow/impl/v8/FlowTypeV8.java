/**
 * FlowTypeV8.java 20.12.2006
 */
package org.dicr.netflow.impl.v8;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * NetFlow Type V8
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public class FlowTypeV8 extends FlowType {
	/** Version code */
	public static final int VERSION = 8;

	/** Flow Type V8 with {@link AggregationScheme#AS} aggregation scheme */
	public static final FlowTypeV8 AS = new FlowTypeV8(AggregationScheme.AS);

	/** Flow Type V8 with {@link AggregationScheme#PROTO_PORT} aggregation scheme */
	public static final FlowTypeV8 PROTO_PORT = new FlowTypeV8(AggregationScheme.PROTO_PORT);

	/** Flow Type V8 with {@link AggregationScheme#PREFIX} aggregation scheme */
	public static final FlowTypeV8 PREFIX = new FlowTypeV8(AggregationScheme.PREFIX);

	/** Flow Type V8 with {@link AggregationScheme#SRC_PREFIX} aggregation scheme */
	public static final FlowTypeV8 SRC_PREFIX = new FlowTypeV8(AggregationScheme.SRC_PREFIX);

	/** Flow Type V8 with {@link AggregationScheme#DST_PREFIX} aggregation scheme */
	public static final FlowTypeV8 DST_PREFIX = new FlowTypeV8(AggregationScheme.DST_PREFIX);

	/**
     * Maximum count of flown in packet (limited only by protocol data unit) But we want to fint into single packet size
     * (65535 bytes)
     */
	public static final int MAX_FLOWS_COUNT = 1000;

	/** Aggregation scheme */
	private AggregationScheme aggScheme = null;

	// Static initializer. Automatically register this flow type
	static {
		registerType(VERSION, AS);
	}

	/**
     * Constructor
     *
     * @param scheme aggregation scheme
     */
	private FlowTypeV8(AggregationScheme scheme) {
		super();
		if (scheme == null) throw new IllegalArgumentException("null scheme");
		this.aggScheme = scheme;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getVersion()
     */
	@Override
	public int getVersion() {
		return VERSION;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getMaxFlowsCount()
     */
	@Override
	public int getMaxFlowsCount() {
		return MAX_FLOWS_COUNT;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getCodec()
     */
	@Override
	public NetFlowCodecV8 getCodec() {
		return NetFlowCodecV8.INSTANCE;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getPacketClass()
     */
	@Override
	public Class<NetFlowPacketV8> getPacketClass() {
		return NetFlowPacketV8.class;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createPacket()
     */
	@Override
	public NetFlowPacketV8 createPacket() {
		return new NetFlowPacketV8(this.aggScheme);
	}

	/**
     * Return aggregation scheme
     *
     * @return aggregation scheme
     */
	public AggregationScheme getAggregationScheme() {
		return this.aggScheme;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getFlowClass()
     */
	@Override
	public Class<? extends FlowV8> getFlowClass() {
		return this.aggScheme != null ? this.aggScheme.getFlowClass() : FlowV8.class;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createFlow()
     */
	@Override
	public FlowV8 createFlow() {
		if (this.aggScheme == null) throw new IllegalStateException(
				"aggregation scheme for this instance not specified");
		return this.aggScheme.createFlow();
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createFlow(org.dicr.traffic.source.TrafficElement)
	 */
	@Override
	public FlowV8 createFlow(TrafficElement element) {
		if (this.aggScheme == null) throw new IllegalStateException(
				"aggregation scheme for this instance not specified");
		return this.aggScheme.createFlow(element);
	}

	/**
     * @see java.lang.Object#hashCode()
     */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.aggScheme == null) ? 0 : this.aggScheme.hashCode());
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
		final FlowTypeV8 other = (FlowTypeV8) obj;
		if (this.aggScheme == null) {
			if (other.aggScheme != null) return false;
		} else if (!this.aggScheme.equals(other.aggScheme)) return false;
		return true;
	}

	/** Convert to string */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{version=" + this.getVersion() + ", aggregation=" + this.aggScheme
				+ "}";
	}
}
