package org.dicr.netflow.impl.v5;

import org.dicr.netflow.impl.v1.*;
import org.dicr.netflow.packet.*;

/**
 * NetFlow Packet version 5.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class NetFlowPacketV5 extends NetFlowPacketV1 implements SequencedPacket {
	/** Maximum value of FlowSequesnce */
	public static final long FLOW_SEQUENCE_VALUE_MAX = 0x0FFFFFFFFL;

	/** Sequence counter of total flows seen */
	private long flowSequence = 0;

	/** Maximum value of EngineType */
	public static final int ENGINE_TYPE_VALUE_MAX = 0x0FF;

	/** Type of flow-switching engine */
	private int engineType = 0;

	/** Maximum value of EngineID */
	public static final int ENGINE_ID_VALUE_MAX = 0x0FF;

	/** Slot number of the flow-switching engine */
	private int engineId = 0;

	/**
     * Constructor
     */
	public NetFlowPacketV5() {
		super();
	}

	/** Return flow type */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV5.INSTANCE;
	}

	/**
     * Return FlowSequence.
     *
     * @return sequence counter of total flows seen.
     */
	public final long getFlowSequence() {
		return this.flowSequence;
	}

	/**
     * Set FlowSequence.
     *
     * @param sequence sequence counter of total flows seen
     * @see #FLOW_SEQUENCE_VALUE_MAX
     */
	public final void setFlowSequence(long sequence) {
		if (sequence < 0 || sequence > FLOW_SEQUENCE_VALUE_MAX) throw new IllegalArgumentException("flowSequence: "
				+ sequence);
		this.flowSequence = sequence;
	}

	/**
     * Return EngineType
     *
     * @return type of flow-switching engine
     */
	public int getEngineType() {
		return this.engineType;
	}

	/**
     * Set EngineType
     *
     * @param type type of flow-switching engine
     * @see #ENGINE_TYPE_VALUE_MAX
     */
	public void setEngineType(int type) {
		if (type < 0 || type > ENGINE_TYPE_VALUE_MAX) throw new IllegalArgumentException("type: " + type);
		this.engineType = type;
	}

	/**
     * Return EngineID.
     *
     * @return slot number of the flow-switching engine
     */
	public int getEngineId() {
		return this.engineId;
	}

	/**
     * Set EngineID
     *
     * @param id slot number of the flow-switching engine
     * @see #ENGINE_ID_VALUE_MAX
     */
	public void setEngineId(int id) {
		if (id < 0 || id > ENGINE_ID_VALUE_MAX) throw new IllegalArgumentException("engineId: " + id);
		this.engineId = id;
	}
}
