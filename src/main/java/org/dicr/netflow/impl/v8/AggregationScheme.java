/**
 * AggregationScheme.java 05.01.2007
 */
package org.dicr.netflow.impl.v8;

import org.dicr.traffic.source.*;

/**
 * Aggregation Scheme
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070105
 */
public enum AggregationScheme {
	/**
     * Router-AS aggregation scheme
     */
	AS(1, FlowV8_RouterAS.class) {
		@Override
		public FlowV8_RouterAS createFlow() {
			return new FlowV8_RouterAS();
		}

		@Override
		public FlowV8_RouterAS createFlow(TrafficElement element) {
			if (element == null) throw new IllegalArgumentException("null traffic element");
			return new FlowV8_RouterAS(element);
		}

		@Override
		public FlowTypeV8 getFlowType() {
			return FlowTypeV8.AS;
		}

	},

	/**
     * Router-Proto-Port aggregation scheme
     */
	PROTO_PORT(2, FlowV8_RouterProtoPort.class) {
		@Override
		public FlowV8_RouterProtoPort createFlow() {
			return new FlowV8_RouterProtoPort();
		}

		@Override
		public FlowV8_RouterProtoPort createFlow(TrafficElement element) {
			if (element == null) throw new IllegalArgumentException("null traffic element");
			return new FlowV8_RouterProtoPort(element);
		}

		@Override
		public FlowTypeV8 getFlowType() {
			return FlowTypeV8.PROTO_PORT;
		}
	},

	/**
     * Router-SourcePrefix aggregation scheme
     */
	SRC_PREFIX(3, FlowV8_RouterSrcPrefix.class) {
		@Override
		public FlowV8_RouterSrcPrefix createFlow() {
			return new FlowV8_RouterSrcPrefix();
		}

		@Override
		public FlowV8_RouterSrcPrefix createFlow(TrafficElement element) {
			if (element == null) throw new IllegalArgumentException("null traffic element");
			return new FlowV8_RouterSrcPrefix(element);
		}

		@Override
		public FlowTypeV8 getFlowType() {
			return FlowTypeV8.SRC_PREFIX;
		}
	},

	/**
     * Router-DestinationPrefix aggregation scheme
     */
	DST_PREFIX(4, FlowV8_RouterDstPrefix.class) {
		@Override
		public FlowV8_RouterDstPrefix createFlow() {
			return new FlowV8_RouterDstPrefix();
		}

		@Override
		public FlowV8_RouterDstPrefix createFlow(TrafficElement element) {
			if (element == null) throw new IllegalArgumentException("null traffic element");
			return new FlowV8_RouterDstPrefix(element);
		}

		@Override
		public FlowTypeV8 getFlowType() {
			return FlowTypeV8.DST_PREFIX;
		}
	},

	/**
     * Router-Prefix aggregation scheme
     */
	PREFIX(5, FlowV8_RouterPrefix.class) {
		@Override
		public FlowV8_RouterSrcPrefix createFlow() {
			return new FlowV8_RouterSrcPrefix();
		}

		@Override
		public FlowV8_RouterPrefix createFlow(TrafficElement element) {
			if (element == null) throw new IllegalArgumentException("null traffic element");
			return new FlowV8_RouterPrefix(element);
		}

		@Override
		public FlowTypeV8 getFlowType() {
			return FlowTypeV8.PREFIX;
		}
	};

	/** Aggregation code */
	private int aggregationCode = 0;

	/** Flow class */
	private Class<? extends FlowV8> flowClass = null;

	/**
     * Constructor
     */
	private AggregationScheme(int code, Class<? extends FlowV8> flowClazz) {
		if (code < 0) throw new IllegalArgumentException("code: " + code);
		if (flowClazz == null) throw new IllegalArgumentException("null flow class");
		this.aggregationCode = code;
		this.flowClass = flowClazz;
	}

	/**
     * Return aggregation scheme by aggregation code
     *
     * @param code aggregation code
     * @return aggregation scheme by specified code or <CODE>null</CODE> if unknown code
     */
	public static AggregationScheme byAggregationCode(int code) {
		for (AggregationScheme scheme : AggregationScheme.values()) {
			if (scheme.getAggregationCode() == code) return scheme;
		}
		return null;
	}

	/**
     * Return aggregation scheme by flow class
     *
     * @param clazz flow implementation class
     * @return aggregation scheme or null if class unknown
     */
	public static AggregationScheme byFlowClass(Class<? extends FlowV8> clazz) {
		if (clazz == null) throw new IllegalArgumentException("null flow class");
		for (AggregationScheme scheme : AggregationScheme.values()) {
			if (scheme.getFlowClass().equals(clazz)) return scheme;
		}
		return null;
	}

	/**
     * Return aggregation code
     *
     * @return aggregation code
     */
	public int getAggregationCode() {
		return this.aggregationCode;
	}

	/**
     * Return flow class
     *
     * @return flow class, which implements this aggregation scheme
     */
	public Class<? extends FlowV8> getFlowClass() {
		return this.flowClass;
	}

	/**
     * Create flow
     *
     * @return created flow for this aggregation schemne implementation
     */
	public abstract FlowV8 createFlow();

	/**
     * Create flow
     *
     * @param element traffic element, from which initialize flow
     * @return created and initialized flow for this aggregation scheme
     */
	public abstract FlowV8 createFlow(TrafficElement element);

	/**
     * Return flow type
     *
     * @return flow type for this aggregation scheme
     */
	public abstract FlowTypeV8 getFlowType();
}
