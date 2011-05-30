/**
 * NetFlowCollector.java 08.01.2007
 */
package org.dicr.netflow.collector;

import org.dicr.netflow.packet.*;

/**
 * NetFlow Collector interface.
 * <P>
 * Listen for NetFlow packets and invoke handlers. Currently it simple extends {@link PacketSource} interface.
 * </P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public interface NetFlowCollector extends PacketSource {
	// NOOP
}
