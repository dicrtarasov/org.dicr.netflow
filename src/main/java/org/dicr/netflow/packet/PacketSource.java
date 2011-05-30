/**
 * PacketSource.java 09.01.2007
 */
package org.dicr.netflow.packet;

import java.util.*;

/**
 * NetFlow Packet source
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public interface PacketSource {
	/**
     * Set packet listeners
     *
     * @param listeners packet listeners
     */
	public void setPacketListeners(Set<PacketListener> listeners);

	/**
     * Add packet listener
     *
     * @param listener listener to add
     */
	public void addListener(PacketListener listener);

	/**
     * Remove packet listener
     *
     * @param listener listener to remove
     */
	public void removeListener(PacketListener listener);
}
