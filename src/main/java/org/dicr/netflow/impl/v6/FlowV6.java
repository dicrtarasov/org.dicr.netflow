/**
 * FlowV6.java 12.01.2007
 */
package org.dicr.netflow.impl.v6;

import org.dicr.netflow.impl.v5.*;
import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;

/**
 * Flow V6
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070112
 */
public class FlowV6 extends FlowV5 {

	/**
	 * Constructor
	 */
	public FlowV6() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param element traffic element to initialize data from
	 */
	public FlowV6(TrafficElement element) {
		super(element);
	}

	/**
	 * Return Flow Type V6 descriptor
	 */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV6.INSTANCE;
	}
}
