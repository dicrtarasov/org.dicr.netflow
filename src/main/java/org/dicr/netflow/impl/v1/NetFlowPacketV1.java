/**
 * NetFlowPacketV1.java
 */
package org.dicr.netflow.impl.v1;

import org.dicr.netflow.packet.*;

/**
 * NetFlow Packet Version 1
 *
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */

public class NetFlowPacketV1 extends NetFlowPacket {
	/** Maximum value of UnixNSecs */
	public static final long UNIX_NSECS_MAX = 0x0FFFFFFFFL;

	/** Residual nanoseconds since 0000 UTC 1970 */
	private long unixNSecs = System.currentTimeMillis() * 1000;

	/**
     * Constructor.
     */
	public NetFlowPacketV1() {
		super();
	}

	/** Return flow type */
	@Override
	public FlowType getFlowType() {
		return FlowTypeV1.INSTANCE;
	}

	/**
     * Set UnixNSecs.
     *
     * @param nanoSeconds residual nanoseconds since 0000 UTC 1970
     * @see #UNIX_NSECS_MAX
     */
	public void setUnixNSecs(long nanoSeconds) {
		if (nanoSeconds < 0 || nanoSeconds > UNIX_NSECS_MAX) throw new IllegalArgumentException("null nanoSeconds");
		this.unixNSecs = nanoSeconds;
	}

	/**
     * Return UnixNSecs.
     *
     * @return residual nanoseconds since 0000 UTC 1970
     */
	public long getUnixNSecs() {
		return this.unixNSecs;
	}
}
