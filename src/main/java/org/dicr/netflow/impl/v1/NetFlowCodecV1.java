/**
 * NetFlowCodecV1.java 20.12.2006
 */
package org.dicr.netflow.impl.v1;

import java.nio.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.codec.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.packet.*;
import org.dicr.util.data.*;

/**
 * NetFlow V1 Codec
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061219
 */
public final class NetFlowCodecV1 extends NetFlowCodec {
	/** Logger */
	private static final Logger log = Logger.getLogger(NetFlowCodecV1.class);

	/** Singleton instance */
	public static final NetFlowCodecV1 INSTANCE = new NetFlowCodecV1();

	/** Hidden constructor */
	private NetFlowCodecV1() {
		super();
	}

	/** Encode flow data to buffer */
	private static void encodeFlow(final FlowV1 flow, final ByteBuffer buf) throws CodecException {
		if (flow == null) throw new IllegalArgumentException("null flow");
		if (buf == null) throw new IllegalArgumentException("null buffer");
		try {
			buf.putInt(flow.getSrcAddress());
			buf.putInt(flow.getDstAddress());
			buf.putInt(flow.getNextHop());
			buf.putShort((short) flow.getInInterface());
			buf.putShort((short) flow.getOutInterface());
			buf.putInt((int) flow.getPacketsCount());
			buf.putInt((int) flow.getBytesCount());
			buf.putInt((int) flow.getFirst());
			buf.putInt((int) flow.getLast());
			buf.putShort((short) flow.getSrcPort());
			buf.putShort((short) flow.getDstPort());
			buf.putShort((short) 0);
			buf.put((byte) flow.getProto());
			buf.put(flow.getTos());
			buf.put(flow.getTcpFlags());
			// pad 7 bytes
			buf.putInt(0);
			buf.putShort((short) 0);
			buf.put((byte) 0);
		} catch (final BufferOverflowException ex) {
			throw new CodecException("data buffer overflow", ex);
		}
	}

	/** Decode flow from data buffer */
	private static FlowV1 decodeFlow(final ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buffer");
		FlowV1 flow = null;
		try {
			flow = new FlowV1();
			flow.setSrcAddress(buf.getInt());
			flow.setDstAddress(buf.getInt());
			flow.setNextHop(buf.getInt());
			flow.setInInterface(ByteUtils.unsigned(buf.getShort()));
			flow.setOutInterface(ByteUtils.unsigned(buf.getShort()));
			flow.setPacketsCount(ByteUtils.unsigned(buf.getInt()));
			flow.setBytesCount(ByteUtils.unsigned(buf.getInt()));
			flow.setFirst(ByteUtils.unsigned(buf.getInt()));
			flow.setLast(ByteUtils.unsigned(buf.getInt()));
			flow.setSrcPort(ByteUtils.unsigned(buf.getShort()));
			flow.setDstPort(ByteUtils.unsigned(buf.getShort()));
			buf.getShort(); // pad1
			flow.setProtocol(ByteUtils.unsigned(buf.get()));
			flow.setTos(buf.get());
			flow.setTcpFlags(buf.get());
			// skip 7 bytes
			buf.getInt();
			buf.getShort();
			buf.get();
		} catch (final BufferUnderflowException ex) {
			throw new CodecException("short buffer data", ex);
		} catch (final IllegalArgumentException ex) {
			throw new CodecException("incorrect buffer data", ex);
		}
		return flow;
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#encode(org.dicr.netflow.packet.NetFlowPacket, java.nio.ByteBuffer)
	 */
	@Override
	public void encodePacket(final NetFlowPacket packet, final ByteBuffer buf) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (!(packet instanceof NetFlowPacketV1)) throw new IllegalArgumentException("can't encode alien packet type: "
		        + packet.getClass());
		if (buf == null) throw new IllegalArgumentException("null buffer");
		final NetFlowPacketV1 packetv1 = (NetFlowPacketV1) packet;
		try {
			// version
			buf.putShort((short) FlowTypeV1.VERSION);

			// flows count
			buf.putShort((short) packetv1.getFlowsCount());

			// time
			buf.putInt((int) packetv1.getSysUptime());
			buf.putInt((int) packetv1.getUnixSecs());
			buf.putInt((int) packetv1.getUnixNSecs());

			// flows
			final Collection<Flow> cFlows = packetv1.getFlows();
			for (final Flow flow : cFlows) {
				NetFlowCodecV1.encodeFlow((FlowV1) flow, buf);
			}
		} catch (final BufferOverflowException ex) {
			throw new CodecException(ex);
		}
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#decode(java.nio.ByteBuffer)
	 */
	@Override
	public NetFlowPacketV1 decodePacket(final ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buffer");
		final NetFlowPacketV1 packet = new NetFlowPacketV1();
		try {
			// version
			final int versionCode = ByteUtils.unsigned(buf.getShort());
			if (versionCode != FlowTypeV1.VERSION) throw new UnsupportedVersionException("packet version "
			        + versionCode + " does not match required version: " + FlowTypeV1.VERSION);

			// flows count
			final int flowsCount = ByteUtils.unsigned(buf.getShort());
			if (flowsCount < 0 || flowsCount > FlowTypeV1.MAX_FLOWS_COUNT) throw new CodecException(
			        "incorrect flows count: " + flowsCount);

			// time
			packet.setSysUptime(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixNSecs(ByteUtils.unsigned(buf.getInt()));

			// flows
			for (int i = 0; i < flowsCount; i++) {
				packet.addFlow(NetFlowCodecV1.decodeFlow(buf));
			}
		} catch (final BufferUnderflowException ex) {
			throw new CodecException("short data buffer", ex);
		} catch (final NetFlowException ex) {
			NetFlowCodecV1.log.fatal("BUG !!! unexpected exception", ex);
			throw new CodecException(ex);
		}
		return packet;
	}
}
