/**
 * NetFlowCodec.java 20.12.2006
 */
package org.dicr.netflow.codec;

import java.nio.*;

import org.dicr.netflow.packet.*;
import org.dicr.util.data.*;

/**
 * NetFlow Codec.
 * <P>
 * Codecs is used to encode/decode packets to/from {@link ByteBuffer}. Each codec provide its own implementation of
 * {@link #encodePacket(NetFlowPacket, ByteBuffer) encode} and {@link #decodePacket(ByteBuffer) decode} methods. Also,
 * it provide static methods to {@link #encode(NetFlowPacket, ByteBuffer) encode} and {@link #decode(ByteBuffer) decode}
 * any packet. To {@link #decode(ByteBuffer) decode} any packet from buffer, it search codec in
 * {@link FlowType#getTypeByVersion(int) registry}. You must {@link FlowType#registerType(int, FlowType) register}
 * packet version/type before decoding it.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061219
 */
public abstract class NetFlowCodec {
	/**
	 * Encode specific packet to buffer.
	 * 
	 * @param packet packet to encode, must be specific type for this codec.
	 * @param buf buffer to encode to
	 * @throws CodecException if packet can't be encoded
	 */
	public abstract void encodePacket(NetFlowPacket packet, ByteBuffer buf) throws CodecException;

	/**
	 * Decode specific packet from buffer.
	 * 
	 * @param buf buffer to decode from
	 * @return decoded packet of specific for this codec version
	 * @throws CodecException decoding exception
	 */
	public abstract NetFlowPacket decodePacket(ByteBuffer buf) throws CodecException;

	/**
	 * Decode any packet from the buffer.
	 * <P>
	 * Decode all known ({@link FlowType#registerType(int, FlowType) registered}) types of packet.
	 * </P>
	 * 
	 * @param buf buffer buffer
	 * @return decoded packet
	 * @throws CodecException codec exception
	 */
	public static NetFlowPacket decode(final ByteBuffer buf) throws CodecException {
		// get version code
		int versionCode = 0;
		try {
			versionCode = ByteUtils.unsigned(buf.getShort(0));
		} catch (final BufferUnderflowException ex) {
			throw new CodecException("short buffer data");
		}

		// search implementation
		final FlowType flowType = FlowType.getTypeByVersion(versionCode);
		if (flowType == null) throw new CodecException("unknown flow version: " + versionCode);

		// decode packet
		return flowType.getCodec().decodePacket(buf);
	}

	/**
	 * Encode any packet to buffer.
	 * <P>
	 * To get codec it use {@link NetFlowPacket#getFlowType()} and {@link FlowType#getCodec()} methods.
	 * 
	 * @param packet packet to encode
	 * @param buf buffer to encode packet to
	 * @throws CodecException codec exception
	 */
	public static void encode(final NetFlowPacket packet, final ByteBuffer buf) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (buf == null) throw new IllegalArgumentException("null buffer");
		packet.getFlowType().getCodec().encodePacket(packet, buf);
	}
}
