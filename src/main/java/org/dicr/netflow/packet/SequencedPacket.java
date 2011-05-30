/**
 * SequencedPacket.java 11.07.2006
 */
package org.dicr.netflow.packet;

import org.dicr.netflow.impl.v7.*;

/**
 * Sequenced Packet. Interface for packets, that support flow sequences. Packet of this type contains sequence field,
 * which describe current number of flows from NetFLow exporter. Collectors use this field to track Flow sequence and
 * analize number lost packets. For example.
 *
 * @see NetFlowPacketV7#getFlowSequence()
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060711
 */
public interface SequencedPacket {
	/**
     * Return FlowSequence.
     *
     * @return sequence counter of total flows seen
     */
	public long getFlowSequence();

	/**
     * Set FlowSequence.
     *
     * @param sequence Sequence counter of total flows seen
     */
	public void setFlowSequence(long sequence);
}
