/**
 * NetFlowPacketV6.java 12.01.2007
 */
package org.dicr.netflow.impl.v6;

import org.dicr.netflow.impl.v5.*;
import org.dicr.netflow.packet.*;

/**
 * NetFlow Packet V6
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070112
 */
public class NetFlowPacketV6 extends NetFlowPacketV5 {

	/** Maximum value of sampling interval */
	public static final int SAMPLIN_INTERVAL_MAX = 0x0FFFF;

	/** First two bits hold the sampling mode; remaining 14 bits hold value of sampling interval */
	private int samplingInterval = 0;

	/**
	 * Constructor
	 */
	public NetFlowPacketV6() {
		super();
	}

	/**
	 * @see org.dicr.netflow.impl.v5.NetFlowPacketV5#getFlowType()
	 */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV6.INSTANCE;
	}

	/**
     * Return sampling interval
     * <P>
     * First two bits hold the sampling mode; remaining 14 bits hold value of sampling interval
     * </P>
     *
     * @return sampling interval
     */
	public int getSamplingInterval() {
		return this.samplingInterval;
	}

	/**
     * Set sampling interval
     * <P>
     * First two bits hold the sampling mode; remaining 14 bits hold value of sampling interval
     * </P>
     *
     * @param interval sempling interval to set
     * @see #SAMPLIN_INTERVAL_MAX
     */
	public void setSamplingInterval(int interval) {
		if (interval < 0 || interval > SAMPLIN_INTERVAL_MAX) throw new IllegalArgumentException("sampling interval: "
				+ interval);
		this.samplingInterval = interval;
	}
}
