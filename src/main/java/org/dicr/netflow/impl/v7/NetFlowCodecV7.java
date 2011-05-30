/**
 * NetFlowCodecV7.java 20.12.2006
 */
package org.dicr.netflow.impl.v7;

import java.nio.*;
import java.util.*;

import org.dicr.netflow.codec.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.packet.*;
import org.dicr.util.data.*;
import org.dicr.util.net.*;

/**
 * NetFlow Codec V7
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public final class NetFlowCodecV7 extends NetFlowCodec {
	/** Singleton instance */
	public static final NetFlowCodecV7 INSTANCE = new NetFlowCodecV7();

	/** Encode flow */
	private static void encodeFlow(FlowV7 flow, ByteBuffer buf) throws CodecException {
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
			buf.put(flow.getFlags1());
			buf.put(flow.getTcpFlags());
			buf.put((byte) flow.getProto());
			buf.put(flow.getTos());
			buf.putShort((short) flow.getSrcAs());
			buf.putShort((short) flow.getDstAs());
			buf.put(Mask.mask2bits(flow.getSrcMask()));
			buf.put(Mask.mask2bits(flow.getDstMask()));
			buf.putShort(flow.getFlags2());
			buf.putInt(flow.getRouterSc());
		} catch (BufferOverflowException ex) {
			throw new CodecException("data buffer overflow", ex);
		} catch (IncorrectAddressException ex) {
			throw new Error("BUG !!!", ex);
		}
	}

	/** Decode flow */
	private static FlowV7 decodeFlow(ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buf");
		FlowV7 flow = new FlowV7();
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
			flow.setFlags1(buf.get());
			flow.setTcpFlags(buf.get());
			flow.setProtocol(ByteUtils.unsigned(buf.get()));
			flow.setTos(buf.get());
			flow.setSrcAs(ByteUtils.unsigned(buf.getShort()));
			flow.setDstAs(ByteUtils.unsigned(buf.getShort()));
			flow.setSrcMask(Mask.bits2mask(buf.get()));
			flow.setDstMask(Mask.bits2mask(buf.get()));
			flow.setFlags2(buf.getShort());
			flow.setRouterSc(buf.getInt());
		} catch (IllegalArgumentException ex) {
			throw new CodecException("incorrect data in buffer", ex);
		} catch (BufferUnderflowException ex) {
			throw new CodecException("short buffer data", ex);
		}
		return flow;
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#encodePacket(org.dicr.netflow.packet.NetFlowPacket, java.nio.ByteBuffer)
	 */
	@Override
	public void encodePacket(NetFlowPacket packet, ByteBuffer buf) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (!(packet instanceof NetFlowPacketV7)) throw new IllegalArgumentException("incorrect packet type: "
		        + packet.getClass());
		if (buf == null) throw new IllegalArgumentException("null buffer");
		NetFlowPacketV7 packetv7 = (NetFlowPacketV7) packet;
		try {
			buf.putShort((short) FlowTypeV7.VERSION);
			buf.putShort((short) packet.getFlowsCount());
			buf.putInt((int) packetv7.getSysUptime());
			buf.putInt((int) packetv7.getUnixSecs());
			buf.putInt((int) packetv7.getUnixNSecs());
			buf.putInt((int) packetv7.getFlowSequence());
			buf.putInt(0);
			Collection<Flow> flows = packetv7.getFlows();
			for (Flow flow : flows) {
				encodeFlow((FlowV7) flow, buf);
			}
		} catch (BufferOverflowException ex) {
			throw new CodecException("data buffer overflow", ex);
		}
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#decodePacket(java.nio.ByteBuffer)
	 */
	@Override
	public NetFlowPacketV7 decodePacket(ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buffer");
		NetFlowPacketV7 packet = new NetFlowPacketV7();
		try {
			// version
			int versionCode = ByteUtils.unsigned(buf.getShort());
			if (versionCode != FlowTypeV7.VERSION) throw new UnsupportedVersionException("packet version "
			        + versionCode + " does not match required version: " + FlowTypeV7.VERSION);

			int count = ByteUtils.unsigned(buf.getShort());
			if (count < 0 || count > FlowTypeV7.MAX_FLOWS_COUNT) throw new NetFlowException("incorrect flows count: "
			        + count);
			packet.setSysUptime(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixNSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setFlowSequence(ByteUtils.unsigned(buf.getInt()));
			buf.getInt(); // skip reserver 4 octets
			for (int i = 0; i < count; i++) {
				packet.addFlow(decodeFlow(buf));
			}
		} catch (IllegalArgumentException ex) {
			throw new CodecException("incorrect buffer data", ex);
		} catch (BufferUnderflowException ex) {
			throw new CodecException("short buffer data", ex);
		} catch (NetFlowException ex) {
			throw new Error("BUG !!!", ex);
		}
		return packet;
	}
}
