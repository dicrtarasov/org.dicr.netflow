/**
 * FlowTypeV5.java 20.12.2006
 */
package org.dicr.netflow.impl.v5;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * FlowType V1
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public final class FlowTypeV5 extends FlowType {
	/** Singleton instance */
	public static final FlowTypeV5 INSTANCE = new FlowTypeV5();

	/** Version code */
	public static final int VERSION = 5;

	/** Maximum number of flow in packet */
	public static final int MAX_FLOWS_COUNT = 30;

	// Static initializer. Automatically register this flow type
	static {
		FlowType.registerType(FlowTypeV5.VERSION, FlowTypeV5.INSTANCE);
	}

	/** Private constructor */
	private FlowTypeV5() {
		super();
	}

	/** Return Version code */
	@Override
	public int getVersion() {
		return FlowTypeV5.VERSION;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#getCodec()
	 */
	@Override
	public NetFlowCodecV5 getCodec() {
		return NetFlowCodecV5.INSTANCE;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#getMaxFlowsCount()
	 */
	@Override
	public int getMaxFlowsCount() {
		return FlowTypeV5.MAX_FLOWS_COUNT;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#getPacketClass()
	 */
	@Override
	public Class<NetFlowPacketV5> getPacketClass() {
		return NetFlowPacketV5.class;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createPacket()
	 */
	@Override
	public NetFlowPacketV5 createPacket() {
		return new NetFlowPacketV5();
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#getFlowClass()
	 */
	@Override
	public Class<FlowV5> getFlowClass() {
		return FlowV5.class;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createFlow()
	 */
	@Override
	public FlowV5 createFlow() {
		return new FlowV5();
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createFlow(org.dicr.traffic.source.TrafficElement)
	 */
	@Override
	public FlowV5 createFlow(final TrafficElement element) {
		if (element == null) throw new IllegalArgumentException("null traffic element");
		return new FlowV5(element);
	}
}
