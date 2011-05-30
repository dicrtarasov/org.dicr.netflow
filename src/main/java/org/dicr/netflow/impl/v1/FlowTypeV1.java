/**
 * FlowTypeV1.java 20.12.2006
 */
package org.dicr.netflow.impl.v1;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * NetFlow Type V1
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061219
 */
public final class FlowTypeV1 extends FlowType {
	/** Version code */
	public static final int VERSION = 1;

	/** Maximum number of flows */
	public static final int MAX_FLOWS_COUNT = 24;

	/** Singleton */
	public static final FlowTypeV1 INSTANCE = new FlowTypeV1();

	// Static initializer. Automatically register this flow type
	static {
		FlowType.registerType(FlowTypeV1.VERSION, FlowTypeV1.INSTANCE);
	}

	/** Private constructor */
	private FlowTypeV1() {
		super();
	}

	/** Return version code */
	@Override
	public int getVersion() {
		return FlowTypeV1.VERSION;
	}

	/** Return codec */
	@Override
	public NetFlowCodecV1 getCodec() {
		return NetFlowCodecV1.INSTANCE;
	}

	/** Return maximum flows count */
	@Override
	public int getMaxFlowsCount() {
		return FlowTypeV1.MAX_FLOWS_COUNT;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#getPacketClass()
	 */
	@Override
	public Class<NetFlowPacketV1> getPacketClass() {
		return NetFlowPacketV1.class;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createPacket()
	 */
	@Override
	public NetFlowPacket createPacket() {
		return new NetFlowPacketV1();
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#getFlowClass()
	 */
	@Override
	public Class<FlowV1> getFlowClass() {
		return FlowV1.class;
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createFlow()
	 */
	@Override
	public FlowV1 createFlow() {
		return new FlowV1();
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createFlow(org.dicr.traffic.source.TrafficElement)
	 */
	@Override
	public FlowV1 createFlow(final TrafficElement element) {
		if (element == null) throw new IllegalArgumentException("null traffic element");
		return new FlowV1(element);
	}
}
