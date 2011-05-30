/**
 * FlowTypeV7.java 20.12.2006
 */
package org.dicr.netflow.impl.v7;

import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * FlowType V7
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public final class FlowTypeV7 extends FlowType {
	/** Version code */
	public static final int VERSION = 7;

	/** Maximum flow count in packet */
	public static final int MAX_FLOWS_COUNT = 1000;

	/** Singleton instance */
	public static final FlowTypeV7 INSTANCE = new FlowTypeV7();

	// Static initializer. Automatically register this flow type
	static {
		registerType(VERSION, INSTANCE);
	}

	/** Hidden constructor */
	private FlowTypeV7() {
		super();
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getVersion()
     */
	@Override
	public int getVersion() {
		return VERSION;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getCodec()
     */
	@Override
	public NetFlowCodecV7 getCodec() {
		return NetFlowCodecV7.INSTANCE;
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
	public Class<NetFlowPacketV7> getPacketClass() {
		return NetFlowPacketV7.class;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createPacket()
     */
	@Override
	public NetFlowPacketV7 createPacket() {
		return new NetFlowPacketV7();
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#getFlowClass()
     */
	@Override
	public Class<FlowV7> getFlowClass() {
		return FlowV7.class;
	}

	/**
     * @see org.dicr.netflow.packet.FlowType#createFlow()
     */
	@Override
	public FlowV7 createFlow() {
		return new FlowV7();
	}

	/**
	 * @see org.dicr.netflow.packet.FlowType#createFlow(org.dicr.traffic.source.TrafficElement)
	 */
	@Override
	public FlowV7 createFlow(TrafficElement element) {
		if (element == null) throw new IllegalArgumentException("null traffic element");
		return new FlowV7(element);
	}
}
