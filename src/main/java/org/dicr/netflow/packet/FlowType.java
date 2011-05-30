/**
 * FlowType.java 14.07.2006
 */
package org.dicr.netflow.packet;

import java.util.*;

import org.dicr.netflow.codec.*;
import org.dicr.traffic.source.*;

/**
 * Flow Type.
 * <P>
 * This is metada type, which describe implementation of NetFlow protocol version. Each version implementation must
 * provide this metadata type, which specify protocol version, {@link NetFlowPacket}, {@link Flow} and
 * {@link NetFlowCodec} implementation classes and other implementation specific parameters.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060714
 */
public abstract class FlowType {
	/** Type registry */
	private static final Map<Integer, FlowType> types = new HashMap<Integer, FlowType>();

	/**
	 * Constructor
	 */
	protected FlowType() {
		super();
	}

	/**
	 * Register flow type/version
	 * 
	 * @param version version of NetFlow
	 * @param type NetFlow type implementation descriptor
	 */
	public static void registerType(int version, FlowType type) {
		if (version < 0) throw new IllegalArgumentException("version: " + version);
		if (type == null) throw new IllegalArgumentException("null type");
		synchronized (types) {
			types.put(Integer.valueOf(version), type);
		}
	}

	/**
	 * Return FlowType implementation by version
	 * 
	 * @param version version of NetFlow protocol
	 * @return flow type implementation descriptor
	 */
	public static FlowType getTypeByVersion(int version) {
		if (version < 0) throw new IllegalArgumentException("version: " + version);
		synchronized (types) {
			return types.get(Integer.valueOf(version));
		}
	}

	/**
	 * Return version code
	 * 
	 * @return NetFlow version code
	 */
	public abstract int getVersion();

	/**
	 * Return codec
	 * 
	 * @return codec to encode/decode packets of this version
	 */
	public abstract NetFlowCodec getCodec();

	/**
	 * Return maximum flows number in packet
	 * 
	 * @return maximum flows count, which can be placed in packet
	 */
	public abstract int getMaxFlowsCount();

	/**
	 * Return packet class
	 * 
	 * @return class, which implement NetFlow packet of this NetFlow version
	 */
	public abstract Class<? extends NetFlowPacket> getPacketClass();

	/**
	 * Create new packet for this NetFlow version
	 * 
	 * @return created packet
	 */
	public abstract NetFlowPacket createPacket();

	/**
	 * Return flow class
	 * 
	 * @return class, which implementf flow of this version
	 */
	public abstract Class<? extends Flow> getFlowClass();

	/**
	 * Create new flow
	 * 
	 * @return created flow of this version
	 */
	public abstract Flow createFlow();

	/**
	 * Create flow from traffic element
	 * <P>
	 * Create flow and imitialize data field from traffic element
	 * </P>
	 * 
	 * @param traffic traffic element to initialize data from
	 * @return created flow of this NetFLow version and initialized from traffic element
	 */
	public abstract Flow createFlow(TrafficElement traffic);

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		return this.getClass().equals(obj.getClass());
	}
	
	@Override
    public int hashCode() {
		return this.getClass().hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{version=" + this.getVersion() + "}";
	}
}
