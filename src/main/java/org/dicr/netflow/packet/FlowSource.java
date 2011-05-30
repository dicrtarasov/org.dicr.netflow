/**
 * FlowSource.java 09.01.2007
 */
package org.dicr.netflow.packet;

import java.util.*;

/**
 * Flow source. The source of {@link Flow}s
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public interface FlowSource {
	/**
     * Set flows listeners
     *
     * @param listeners listeners to set
     */
	public void setFlowListeners(Set<FlowListener> listeners);

	/**
     * Add flows listener
     *
     * @param listener listener to add
     */
	public void addListener(FlowListener listener);

	/**
     * Remove flows listener
     *
     * @param listener listener to remove
     */
	public void removeListener(FlowListener listener);
}
