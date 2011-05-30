/**
 * NetFlowCodecV8.java 20.12.2006
 */
package org.dicr.netflow.impl.v8;

import java.nio.*;
import java.util.*;

import org.dicr.netflow.codec.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.packet.*;
import org.dicr.util.data.*;
import org.dicr.util.net.*;

/**
 * NetFlow Codec V8
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public class NetFlowCodecV8 extends NetFlowCodec {
	/** Singleton instance */
	public static final NetFlowCodecV8 INSTANCE = new NetFlowCodecV8();

	/** Hidden constructor */
	private NetFlowCodecV8() {
		super();
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#encodePacket(org.dicr.netflow.packet.NetFlowPacket, java.nio.ByteBuffer)
	 */
	@Override
	public void encodePacket(NetFlowPacket packet, ByteBuffer buf) throws CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");
		if (!(packet instanceof NetFlowPacketV8)) throw new UnsupportedVersionException("unsupported packet type: "
		        + packet.getClass());
		if (buf == null) throw new IllegalArgumentException("null buffer");
		NetFlowPacketV8 packet8 = (NetFlowPacketV8) packet;
		AggregationScheme scheme = packet8.getAggregationScheme();
		FlowV8Codec codec8 = FlowV8Codec.byAggregationScheme(scheme);
		if (codec8 == null) throw new UnsupportedVersionException(
		        "BUG !!! no codec V8 specified for aggregation scheme: " + scheme);
		try {
			buf.putShort((short) FlowTypeV8.VERSION);
			buf.putShort((short) packet8.getFlowsCount());
			buf.putInt((int) packet8.getSysUptime());
			buf.putInt((int) packet8.getUnixSecs());
			buf.putInt((int) packet8.getUnixNSecs());
			buf.putInt((int) packet8.getFlowSequence());
			buf.put((byte) packet8.getEngineType());
			buf.put((byte) packet8.getEngineId());
			buf.put((byte) scheme.getAggregationCode());
			buf.put((byte) packet8.getAggregationVersion());
			buf.putInt(0);
			Collection<Flow> flows = packet8.getFlows();
			for (Flow flow : flows) {
				codec8.encodeFlow((FlowV8) flow, buf);
			}
		} catch (BufferOverflowException ex) {
			throw new CodecException("short buffer data", ex);
		}
	}

	/**
	 * @see org.dicr.netflow.codec.NetFlowCodec#decodePacket(java.nio.ByteBuffer)
	 */
	@Override
	public NetFlowPacketV8 decodePacket(ByteBuffer buf) throws CodecException {
		if (buf == null) throw new IllegalArgumentException("null buffer");
		NetFlowPacketV8 packet = new NetFlowPacketV8();
		try {
			int versionCode = ByteUtils.unsigned(buf.getShort());
			if (versionCode != FlowTypeV8.VERSION) throw new UnsupportedVersionException("packet version "
			        + versionCode + " does not match required version: " + FlowTypeV8.VERSION);
			int count = ByteUtils.unsigned(buf.getShort());
			if (count < 0 || count > FlowTypeV8.MAX_FLOWS_COUNT) throw new CodecException("incorrect flows count: "
			        + count);
			packet.setSysUptime(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setUnixNSecs(ByteUtils.unsigned(buf.getInt()));
			packet.setFlowSequence(ByteUtils.unsigned(buf.getInt()));
			packet.setEngineType(ByteUtils.unsigned(buf.get()));
			packet.setEngineId(ByteUtils.unsigned(buf.get()));
			int aggCode = ByteUtils.unsigned(buf.get());
			AggregationScheme scheme = AggregationScheme.byAggregationCode(aggCode);
			if (scheme == null) throw new CodecException("unknown aggregation scheme code: " + aggCode);
			packet.setAggregationScheme(scheme);
			packet.setAggregationVersion(ByteUtils.unsigned(buf.get()));
			buf.getInt(); // skip 4 reserved octets
			// decode flows
			FlowV8Codec codec = FlowV8Codec.byAggregationScheme(scheme);
			if (codec == null) throw new UnsupportedVersionException(
			        "BUG !!! no codec defined for aggregation scheme: " + scheme);
			for (int i = 0; i < count; i++) {
				packet.addFlow(codec.decodeFlow(buf));
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

	/**
	 * Flow codec
	 */
	protected static abstract class FlowV8Codec {
		private static final Map<AggregationScheme, FlowV8Codec> codecs = new HashMap<AggregationScheme, FlowV8Codec>();

		/** Static map aggregation code to flow codec */
		static {
			codecs.put(AggregationScheme.AS, Codec_AS.CODEC_INSTANCE);
			codecs.put(AggregationScheme.SRC_PREFIX, Codec_SrcPrefix.CODEC_INSTANCE);
			codecs.put(AggregationScheme.DST_PREFIX, Codec_DstPrefix.CODEC_INSTANCE);
			codecs.put(AggregationScheme.PREFIX, Codec_Prefix.CODEC_INSTANCE);
			codecs.put(AggregationScheme.PROTO_PORT, Codec_ProtoPort.CODEC_INSTANCE);
		}

		/**
		 * Return flow codec by aggregation scheme
		 * 
		 * @param scheme aggregation scheme
		 * @return flow codec for this aggregation scheme
		 */
		public static FlowV8Codec byAggregationScheme(AggregationScheme scheme) {
			if (scheme == null) throw new IllegalArgumentException("null scheme");
			FlowV8Codec result = codecs.get(scheme);
			if (result == null) throw new Error("BUG !!! Unknown aggregation scheme: " + scheme);
			return result;
		}

		/**
		 * Encode flow
		 * 
		 * @param flow flow to encode
		 * @param buf buffer to encode to
		 * @throws CodecException error encoding
		 */
		protected abstract void encodeFlow(FlowV8 flow, ByteBuffer buf) throws CodecException;

		/**
		 * Decode flow
		 * 
		 * @param buf buffer to decode from
		 * @return decoded flow
		 * @throws CodecException decode exception
		 */
		protected abstract FlowV8 decodeFlow(ByteBuffer buf) throws CodecException;
	}

	/**
	 * AS flow codec
	 */
	protected static class Codec_AS extends FlowV8Codec {
		/** Singleton instance */
		public static final Codec_AS CODEC_INSTANCE = new Codec_AS();

		/** Private constructor */
		private Codec_AS() {
			super();
		}

		/** Encode flow */
		@Override
		protected void encodeFlow(FlowV8 flow, ByteBuffer buf) throws CodecException {
			if (flow == null) throw new IllegalArgumentException("null flow");
			if (buf == null) throw new IllegalArgumentException("null buffer");
			if (!(flow instanceof FlowV8_RouterAS)) throw new UnsupportedVersionException("unsupported flow type: "
			        + flow.getClass());
			FlowV8_RouterAS flow8 = (FlowV8_RouterAS) flow;
			try {
				buf.putInt((int) flow.getFlowsAggregated());
				buf.putInt((int) flow.getPacketsCount());
				buf.putInt((int) flow.getBytesCount());
				buf.putInt((int) flow.getFirst());
				buf.putInt((int) flow.getLast());
				buf.putShort((short) flow8.getSrcAs());
				buf.putShort((short) flow8.getDstAs());
				buf.putShort((short) flow8.getInputInterface());
				buf.putShort((short) flow8.getOutputInterface());
			} catch (BufferOverflowException ex) {
				throw new CodecException("data buffer overflow", ex);
			}
		}

		/** Decode flow */
		@Override
		protected FlowV8_RouterAS decodeFlow(ByteBuffer buf) throws CodecException {
			if (buf == null) throw new IllegalArgumentException("null buffer");
			FlowV8_RouterAS flow = new FlowV8_RouterAS();
			try {
				flow.setFlowsAggregated(ByteUtils.unsigned(buf.getInt()));
				flow.setPacketsCount(ByteUtils.unsigned(buf.getInt()));
				flow.setBytesCount(ByteUtils.unsigned(buf.getInt()));
				flow.setFirst(ByteUtils.unsigned(buf.getInt()));
				flow.setLast(ByteUtils.unsigned(buf.getInt()));
				flow.setSrcAs(ByteUtils.unsigned(buf.getShort()));
				flow.setDstAs(ByteUtils.unsigned(buf.getShort()));
				flow.setInputInterface(ByteUtils.unsigned(buf.getShort()));
				flow.setOutputInterface(ByteUtils.unsigned(buf.getShort()));
			} catch (IllegalArgumentException ex) {
				throw new CodecException("incorrect buffer data", ex);
			} catch (BufferUnderflowException ex) {
				throw new CodecException("short buffer data", ex);
			}
			return flow;
		}
	}

	/**
	 * Src flow Codec
	 */
	protected static class Codec_SrcPrefix extends FlowV8Codec {
		/** Singleton instance */
		public static final Codec_SrcPrefix CODEC_INSTANCE = new Codec_SrcPrefix();

		/** Private constructor */
		private Codec_SrcPrefix() {
			super();
		}

		/** Encode flow */
		@Override
		protected void encodeFlow(FlowV8 flow, ByteBuffer buf) throws CodecException {
			if (flow == null) throw new IllegalArgumentException("null flow");
			if (buf == null) throw new IllegalArgumentException("null buffer");
			if (!(flow instanceof FlowV8_RouterSrcPrefix)) throw new UnsupportedVersionException(
			        "unsupported flow type: " + flow.getClass());
			FlowV8_RouterSrcPrefix flow8 = (FlowV8_RouterSrcPrefix) flow;
			try {
				buf.putInt((int) flow8.getFlowsAggregated());
				buf.putInt((int) flow8.getPacketsCount());
				buf.putInt((int) flow8.getBytesCount());
				buf.putInt((int) flow8.getFirst());
				buf.putInt((int) flow8.getLast());
				buf.putInt(flow8.getSrcPrefix());
				buf.put(Mask.mask2bits(flow8.getSrcMask()));
				buf.put((byte) 0);
				buf.putShort((short) flow8.getSrcAs());
				buf.putShort((short) flow8.getIfInput());
				buf.putShort((short) 0);
			} catch (BufferOverflowException ex) {
				throw new CodecException("data buffer overflow", ex);
			} catch (IncorrectAddressException ex) {
				throw new Error("BUG !!!", ex);
			}
		}

		/** Decode flow */
		@Override
		protected FlowV8 decodeFlow(ByteBuffer buf) throws CodecException {
			if (buf == null) throw new IllegalArgumentException("null buffer");
			FlowV8_RouterSrcPrefix flow = new FlowV8_RouterSrcPrefix();
			try {
				flow.setFlowsAggregated(ByteUtils.unsigned(buf.getInt()));
				flow.setPacketsCount(ByteUtils.unsigned(buf.getInt()));
				flow.setBytesCount(ByteUtils.unsigned(buf.getInt()));
				flow.setFirst(ByteUtils.unsigned(buf.getInt()));
				flow.setLast(ByteUtils.unsigned(buf.getInt()));
				flow.setSrcPrefix(buf.getInt());
				flow.setSrcMask(Mask.bits2mask(buf.get()));
				buf.get();
				flow.setSrcAs(ByteUtils.unsigned(buf.getShort()));
				flow.setIfInput(ByteUtils.unsigned(buf.getShort()));
				buf.getShort();
			} catch (IllegalArgumentException ex) {
				throw new CodecException("incorrect buffer data", ex);
			} catch (BufferUnderflowException ex) {
				throw new CodecException("short buffer data", ex);
			}
			return flow;
		}

	}

	/**
	 * DST flow Codec
	 */
	protected static class Codec_DstPrefix extends FlowV8Codec {
		/** Singleton instance */
		public static final Codec_DstPrefix CODEC_INSTANCE = new Codec_DstPrefix();

		/** Private constructor */
		private Codec_DstPrefix() {
			super();
		}

		/** Encode flow */
		@Override
		protected void encodeFlow(FlowV8 flow, ByteBuffer buf) throws CodecException {
			if (flow == null) throw new IllegalArgumentException("null flow");
			if (buf == null) throw new IllegalArgumentException("null buffer");
			if (!(flow instanceof FlowV8_RouterDstPrefix)) throw new UnsupportedVersionException(
			        "unsupported flow type: " + flow.getClass());
			FlowV8_RouterDstPrefix flowv8 = (FlowV8_RouterDstPrefix) flow;
			try {
				buf.putInt((int) flowv8.getFlowsAggregated());
				buf.putInt((int) flowv8.getPacketsCount());
				buf.putInt((int) flowv8.getBytesCount());
				buf.putInt((int) flowv8.getFirst());
				buf.putInt((int) flowv8.getLast());
				buf.putInt(flowv8.getDstPrefix());
				buf.put(Mask.mask2bits(flowv8.getDstMask()));
				buf.put((byte) 0);
				buf.putShort((short) flowv8.getDstAs());
				buf.putShort((short) flowv8.getIfOutput());
				buf.putShort((short) 0);
			} catch (BufferOverflowException ex) {
				throw new CodecException("data buffer overflow", ex);
			} catch (IncorrectAddressException ex) {
				throw new Error("BUG !!!", ex);
			}
		}

		/** Decode flow */
		@Override
		protected FlowV8 decodeFlow(ByteBuffer buf) throws CodecException {
			if (buf == null) throw new IllegalArgumentException("null buffer");
			FlowV8_RouterDstPrefix flow = new FlowV8_RouterDstPrefix();
			try {
				flow.setFlowsAggregated(ByteUtils.unsigned(buf.getInt()));
				flow.setPacketsCount(ByteUtils.unsigned(buf.getInt()));
				flow.setBytesCount(ByteUtils.unsigned(buf.getInt()));
				flow.setFirst(ByteUtils.unsigned(buf.getInt()));
				flow.setLast(ByteUtils.unsigned(buf.getInt()));
				flow.setDstPrefix(buf.getInt());
				flow.setDstMask(Mask.bits2mask(buf.get()));
				buf.get();
				flow.setDstAs(ByteUtils.unsigned(buf.getShort()));
				flow.setIfOutput(ByteUtils.unsigned(buf.getShort()));
				buf.getShort();
			} catch (IllegalArgumentException ex) {
				throw new CodecException("incorrect buffer data", ex);
			} catch (BufferUnderflowException ex) {
				throw new CodecException("short buffer data", ex);
			}
			return flow;
		}
	}

	/**
	 * Prefix flow Codec
	 */
	protected static class Codec_Prefix extends FlowV8Codec {
		/** Singleton instance */
		public static final Codec_Prefix CODEC_INSTANCE = new Codec_Prefix();

		/** Private constructor */
		private Codec_Prefix() {
			super();
		}

		/** Encode flow */
		@Override
		protected void encodeFlow(FlowV8 flow, ByteBuffer buf) throws CodecException {
			if (flow == null) throw new IllegalArgumentException("null flow");
			if (buf == null) throw new IllegalArgumentException("null buffer");
			if (!(flow instanceof FlowV8_RouterPrefix)) throw new IllegalArgumentException("unsupported flow type: "
			        + flow.getClass());
			FlowV8_RouterPrefix flow8 = (FlowV8_RouterPrefix) flow;
			try {
				buf.putInt((int) flow8.getFlowsAggregated());
				buf.putInt((int) flow8.getPacketsCount());
				buf.putInt((int) flow8.getBytesCount());
				buf.putInt((int) flow8.getFirst());
				buf.putInt((int) flow8.getLast());
				buf.putInt(flow8.getSrcPrefix());
				buf.putInt(flow8.getDstPrefix());
				buf.put(Mask.mask2bits(flow8.getDstMask()));
				buf.put(Mask.mask2bits(flow8.getSrcMask()));
				buf.putShort((short) 0);
				buf.putShort((short) flow8.getSrcAs());
				buf.putShort((short) flow8.getDstAs());
				buf.putShort((short) flow8.getIfInput());
				buf.putShort((short) flow8.getIfOutput());
			} catch (BufferOverflowException ex) {
				throw new CodecException("data buffer overflow", ex);
			} catch (IncorrectAddressException ex) {
				throw new Error("BUG !!!", ex);
			}
		}

		/** Decode flow */
		@Override
		protected FlowV8 decodeFlow(ByteBuffer buf) throws CodecException {
			if (buf == null) throw new IllegalArgumentException("null buffer");
			FlowV8_RouterPrefix flow = new FlowV8_RouterPrefix();
			try {
				flow.setFlowsAggregated(ByteUtils.unsigned(buf.getInt()));
				flow.setPacketsCount(ByteUtils.unsigned(buf.getInt()));
				flow.setBytesCount(ByteUtils.unsigned(buf.getInt()));
				flow.setFirst(ByteUtils.unsigned(buf.getInt()));
				flow.setLast(ByteUtils.unsigned(buf.getInt()));
				flow.setSrcPrefix(buf.getInt());
				flow.setDstPrefix(buf.getInt());
				flow.setDstMask(Mask.bits2mask(buf.get()));
				flow.setSrcMask(Mask.bits2mask(buf.get()));
				buf.getShort();
				flow.setSrcAs(ByteUtils.unsigned(buf.getShort()));
				flow.setDstAs(ByteUtils.unsigned(buf.getShort()));
				flow.setIfInput(ByteUtils.unsigned(buf.getShort()));
				flow.setIfOutput(ByteUtils.unsigned(buf.getShort()));
			} catch (IllegalArgumentException ex) {
				throw new CodecException("incorrect buffer data", ex);
			} catch (BufferUnderflowException ex) {
				throw new CodecException("short buffer data", ex);
			}
			return flow;
		}
	}

	/**
	 * ProtoPort flow Codec
	 */
	protected static class Codec_ProtoPort extends FlowV8Codec {
		/** Singleton instance */
		public static final Codec_ProtoPort CODEC_INSTANCE = new Codec_ProtoPort();

		/** Private constructor */
		private Codec_ProtoPort() {
			super();
		}

		/** Encode flow */
		@Override
		protected void encodeFlow(FlowV8 flow, ByteBuffer buf) throws CodecException {
			if (flow == null) throw new IllegalArgumentException("null flow");
			if (buf == null) throw new IllegalArgumentException("null buffer");
			if (!(flow instanceof FlowV8_RouterProtoPort)) throw new UnsupportedVersionException(
			        "unsupported flow type: " + flow.getClass());
			FlowV8_RouterProtoPort flow8 = (FlowV8_RouterProtoPort) flow;
			try {
				buf.putInt((int) flow8.getFlowsAggregated());
				buf.putInt((int) flow8.getPacketsCount());
				buf.putInt((int) flow8.getBytesCount());
				buf.putInt((int) flow8.getFirst());
				buf.putInt((int) flow8.getLast());
				buf.put((byte) flow8.getProto());
				buf.put((byte) 0);
				buf.putShort((short) 0);
				buf.putShort((short) flow8.getSrcPort());
				buf.putShort((short) flow8.getDstPort());
			} catch (BufferOverflowException ex) {
				throw new CodecException("data buffer overflow", ex);
			}
		}

		/** Decode flow */
		@Override
		protected FlowV8 decodeFlow(ByteBuffer buf) throws CodecException {
			if (buf == null) throw new IllegalArgumentException("null buf");
			FlowV8_RouterProtoPort flow = new FlowV8_RouterProtoPort();
			try {
				flow.setFlowsAggregated(ByteUtils.unsigned(buf.getInt()));
				flow.setPacketsCount(ByteUtils.unsigned(buf.getInt()));
				flow.setBytesCount(ByteUtils.unsigned(buf.getInt()));
				flow.setFirst(ByteUtils.unsigned(buf.getInt()));
				flow.setLast(ByteUtils.unsigned(buf.getInt()));
				flow.setProto(ByteUtils.unsigned(buf.get()));
				buf.get();
				buf.getShort();
				flow.setSrcPort(ByteUtils.unsigned(buf.getShort()));
				flow.setDstPort(ByteUtils.unsigned(buf.getShort()));
			} catch (IllegalArgumentException ex) {
				throw new CodecException("incorrect buffer data", ex);
			} catch (BufferUnderflowException ex) {
				throw new CodecException("data buffer overflow", ex);
			}
			return flow;
		}
	}
}
