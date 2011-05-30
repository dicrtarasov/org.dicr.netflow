package org.dicr.netflow.impl.v7;

import org.dicr.netflow.impl.v1.*;
import org.dicr.netflow.packet.*;

/**
 * NetFlow Packet version 7.
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class NetFlowPacketV7 extends NetFlowPacketV1 implements SequencedPacket {
	/** Maximum value of FlowSequence */
	public static final long FLOW_SEQUENCE_VALUE_MAX = 0x0FFFFFFFFL;

	/** Sequence counter of total flows seen */
	private long flowSequence = 0;

	/**
     * Constructor.
     */
	public NetFlowPacketV7() {
		super();
	}

	/**
     * Return flow type
     *
     * @see org.dicr.netflow.packet.NetFlowPacket#getFlowType()
     */
	@Override
	public FlowTypeV7 getFlowType() {
		return FlowTypeV7.INSTANCE;
	}

	/**
     * Return FlowSequence.
     *
     * @return sequence counter of total flows seen
     */
	public final long getFlowSequence() {
		return this.flowSequence;
	}

	/**
     * Set FlowSequence.
     *
     * @param sequence Sequence counter of total flows seen
     * @see #FLOW_SEQUENCE_VALUE_MAX
     */
	public final void setFlowSequence(long sequence) {
		if (sequence < 0 || sequence > FLOW_SEQUENCE_VALUE_MAX) throw new IllegalArgumentException("flowSequence: "
				+ sequence);
		this.flowSequence = sequence;
	}
}
