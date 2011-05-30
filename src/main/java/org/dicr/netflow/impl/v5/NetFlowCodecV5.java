/**
 * NetFlowCodecV5.java 20.12.2006
 */
package org.dicr.netflow.impl.v5;

import java.nio.*;
import java.util.*;

import org.dicr.netflow.codec.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.packet.*;
import org.dicr.util.data.*;
import org.dicr.util.net.*;

/**
 * NetFlow Codec V5
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public final class NetFlowCodecV5 extends NetFlowCodec {
	/** Singleton instance */
	public static final NetFlowCodecV5 INSTANCE = new NetFlowCodecV5();

	/** Private constructor */
	private NetFlowCodecV5() {
		super();
	}

	/** Encode flow */
	private static void encodeFlow(FlowV5 flow, ByteBuffer buf) throws CodecException {
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
			buf.put((byte) 0);
			buf.put(flow.getTcpFlags());
			buf.put((byte) flow.getProto());
			buf.put(flow.getTos());
			buf.putShort((short) flow.getSrcAs());
			buf.putShort((short) flow.getDstAs());
			buf.put(Mask.mask2bits(flow.getSrcMask()));
			buf.put(Mask.mask2bits(flow.getDstMask()));
			buf.putShort((short) 0);
		} catch (BufferOverflowException ex) {
			throw new CodecException("data buffer overflow", ex);
		} catch (IncorrectAddressException ex) {
			throw new Error("BUG !!!", ex);
		}
	}

	/** Decode flow */
	private static FlowV5 decodeFlow(ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buf");
		FlowV5 flow = new FlowV5();
		try {
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
			buf.get(); // skip unused byte
			flow.setTcpFlags(buf.get());
			flow.setProtocol(ByteUtils.unsigned(buf.get()));
			flow.setTos(buf.get());
			flow.setSrcAs(ByteUtils.unsigned(buf.getShort()));
			flow.setDstAs(ByteUtils.unsigned(buf.getShort()));
			flow.setSrcMask(Mask.bits2mask(buf.get()));
			flow.setDstMask(Mask.bits2mask(buf.get()));
			buf.getShort(); // skip unused byte
		} catch (IllegalArgumentException ex) {
			throw new CodecException("incorrect data in buffer", ex);
		} catch (BufferUnderflowException ex) {
			throw new CodecException("data buffer overflow", ex);
		}
		return flow;
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#encodePacket(org.dicr.netflow.packet.NetFlowPacket, java.nio.ByteBuffer)
	 */
	@Override
	public void encodePacket(NetFlowPacket packet, ByteBuffer buf) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (!(packet instanceof NetFlowPacketV5)) throw new IllegalArgumentException("incorrect packet type: "
		        + packet.getClass());
		if (buf == null) throw new IllegalArgumentException("null buffer");
		NetFlowPacketV5 packetv5 = (NetFlowPacketV5) packet;
		try {
			buf.putShort((short) FlowTypeV5.VERSION);
			buf.putShort((short) packetv5.getFlowsCount());
			buf.putInt((int) packetv5.getSysUptime());
			buf.putInt((int) packetv5.getUnixSecs());
			buf.putInt((int) packetv5.getUnixNSecs());
			buf.putInt((int) packetv5.getFlowSequence());
			buf.put((byte) packetv5.getEngineType());
			buf.put((byte) packetv5.getEngineId());
			buf.putShort((short) 0);
			Collection<Flow> flows = packetv5.getFlows();
			for (Flow flow : flows) {
				encodeFlow((FlowV5) flow, buf);
			}
		} catch (BufferOverflowException ex) {
			throw new CodecException("data buffer overflow", ex);
		}
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#decodePacket(java.nio.ByteBuffer)
	 */
	@Override
	public NetFlowPacketV5 decodePacket(ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buffer");
		NetFlowPacketV5 packet = new NetFlowPacketV5();
		try {
			// version
			int versionCode = ByteUtils.unsigned(buf.getShort());
			if (versionCode != FlowTypeV5.VERSION) throw new UnsupportedVersionException("packet version "
			        + versionCode + " does not match required version: " + FlowTypeV5.VERSION);

			int count = ByteUtils.unsigned(buf.getShort());
			if (count < 0 || count > FlowTypeV5.MAX_FLOWS_COUNT) throw new NetFlowException("incorrect flows count: "
			        + count);
			packet.setSysUptime(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixNSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setFlowSequence(ByteUtils.unsigned(buf.getInt()));
			packet.setEngineType(ByteUtils.unsigned(buf.get()));
			packet.setEngineId(ByteUtils.unsigned(buf.get()));
			buf.getShort();
			// decode flows
			for (int i = 0; i < count; i++) {
				packet.addFlow(decodeFlow(buf));
			}
		} catch (IllegalArgumentException ex) {
			throw new CodecException("incorrect data in buffer", ex);
		} catch (BufferUnderflowException ex) {
			throw new CodecException("short data buffer", ex);
		} catch (NetFlowException ex) {
			throw new CodecException("BUG !!!", ex);
		}
		return packet;
	}
}
