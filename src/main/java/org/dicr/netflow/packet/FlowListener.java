/**
 * FlowListener.java 09.01.2007
 */
package org.dicr.netflow.packet;

import java.util.*;

/**
 * Flow Listener.
 * <P>Listen and process {@link Flow}s from {@link FlowSource}
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public interface FlowListener {

	/**
     * Process imcoming flows from {@link FlowSource}
     *
     * @param flows incoming flows to process
     */
	public void processFlows(Collection<? extends Flow> flows);
}
