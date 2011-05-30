/**
 * PacketListener.java 09.01.2007
 */
package org.dicr.netflow.packet;

/**
 * NetFlow Packet listener.
 * <P>Listen and process {@link NetFlowPacket}s from {@link PacketSource}</P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070102
 */
public interface PacketListener {
	/**
	 * Process packet
	 *
	 * @param packet packet from {@link PacketSource}
	 */
	public void processPacket(NetFlowPacket packet);
}
