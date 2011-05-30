/**
 * FlowTypeV65.java 12.01.2007
 */
package org.dicr.netflow.impl.v6;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * FlowType V1
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public final class FlowTypeV6 extends FlowType {
	/** Singleton instance */
	public static final FlowTypeV6 INSTANCE = new FlowTypeV6();

	/** Version code */
	public static final int VERSION = 6;

	/** Maximum number of flow in packet */
	public static final int MAX_FLOWS_COUNT = 30;

	// Static initializer. Automatically register this flow type
	static {
		registerType(VERSION, INSTANCE);
	}

	/** Private constructor */
	private FlowTypeV6() {
		super();
	}

	/** Return Version code */
	@Override
	public int getVersion() {
		return VERSION;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getCodec()
     */
	@Override
	public NetFlowCodecV6 getCodec() {
		return NetFlowCodecV6.INSTANCE;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getMaxFlowsCount()
     */
	@Override
	public int getMaxFlowsCount() {
		return MAX_FLOWS_COUNT;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getPacketClass()
     */
	@Override
	public Class<NetFlowPacketV6> getPacketClass() {
		return NetFlowPacketV6.class;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createPacket()
     */
	@Override
	public NetFlowPacketV6 createPacket() {
		return new NetFlowPacketV6();
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getFlowClass()
     */
	@Override
	public Class<FlowV6> getFlowClass() {
		return FlowV6.class;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createFlow()
     */
	@Override
	public FlowV6 createFlow() {
		return new FlowV6();
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createFlow(org.dicr.traffic.source.TrafficElement)
     */
	@Override
	public FlowV6 createFlow(TrafficElement element) {
		if (element == null) throw new IllegalArgumentException("null traffic element");
		return new FlowV6(element);
	}
}
