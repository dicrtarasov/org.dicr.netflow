/**
 * AbstractExporter.java 08.01.2007
 */
package org.dicr.netflow.exporter.impl;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.codec.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.exporter.*;
import org.dicr.netflow.packet.*;

/**
 * Abstract Exporter Channel.
 * <P>
 * Implement common used methods.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public abstract class AbstractExporter implements NetFlowExporter {
	/** Logger */
	private static final Logger log = Logger.getLogger(AbstractExporter.class);

	/** Current flows sequence */
	private long flowSequence = 0;

	/**
	 * Generate next flow sequence for packet.
	 * 
	 * @param flowsCount count of flows in next packet
	 * @return flows sequence for packet
	 */
	private long nextSequence(final int flowsCount) {
		synchronized (this) {
			this.flowSequence += flowsCount;
			return this.flowSequence;
		}
	}

	/**
	 * Build packets from flows. Each packet contains no more then {@link FlowType#getMaxFlowsCount()} flows.
	 * 
	 * @param flows flows to add in packets
	 * @return created packets with added flows
	 * @throws NetFlowException error adding flow to created packet
	 */
	public Collection<NetFlowPacket> buildPackets(final Collection<? extends Flow> flows) throws NetFlowException {
		if (flows == null) throw new IllegalArgumentException("null flows");
		// result list of packets
		final Collection<NetFlowPacket> packets = new ArrayList<NetFlowPacket>();
		// skip if empty
		if (flows.isEmpty()) {
			AbstractExporter.log.debug("no flows to build packets");
			return packets;
		}

		// types
		FlowType flowType = null;
		NetFlowPacket packet = null;

		// iterate all flows
		final Iterator<? extends Flow> flowsIterator = flows.iterator();
		while (flowsIterator.hasNext()) {
			final Flow flow = flowsIterator.next();

			// check flow type
			if (flowType == null) flowType = flow.getFlowType();
			else if (!flow.getFlowType().equals(flowType)) throw new NetFlowException("incorrect flow type: "
			        + flow.getFlowType());

			// check current packet
			if (packet == null) packet = flowType.createPacket();
			else if (packet.getFlowsCount() >= flowType.getMaxFlowsCount()) {
				// recreate new packet
				if (packet instanceof SequencedPacket) ((SequencedPacket) packet).setFlowSequence(this.nextSequence(packet.getFlowsCount()));
				packets.add(packet);
				packet = flowType.createPacket();
			}

			// add flow to packet
			packet.addFlow(flow);
		}

		// add last packet
		if (packet != null && packet.getFlowsCount() > 0) {
			if (packet instanceof SequencedPacket) ((SequencedPacket) packet).setFlowSequence(this.nextSequence(packet.getFlowsCount()));
			packets.add(packet);
		}
		return packets;
	}

	/**
	 * Export packets to destination. For each packet call {@link #export(NetFlowPacket)}.
	 * 
	 * @param packets packets to export
	 * @throws IOException IO exception in channel
	 * @throws CodecException packet encoding problem
	 */
	public void exportPackets(final Collection<NetFlowPacket> packets) throws IOException, CodecException {
		if (packets == null) throw new IllegalArgumentException("null packets");
		if (packets.isEmpty()) AbstractExporter.log.debug("no packets to export");
		else {
			for (final NetFlowPacket packet : packets) {
				this.export(packet);
			}
		}
	}

	/**
	 * Export flows to destination. First, this methoid call {@link #buildPackets(Collection) build} method to construct
	 * packets from specified flows. Then call {@link #exportPackets(Collection)} for each packet.
	 * 
	 * @param flows flows to export.
	 * @throws IOException I/O error exporting flows
	 * @throws NetFlowException error while {@link NetFlowException building} or {@link CodecException encoding} packet
	 */
	public void export(final Collection<? extends Flow> flows) throws IOException, NetFlowException {
		if (flows == null) throw new IllegalArgumentException("null flows");
		if (flows.isEmpty()) AbstractExporter.log.trace("no flows to export");
		else {
			// prepare packets to send
			final Collection<NetFlowPacket> packets = this.buildPackets(flows);
			if (packets.isEmpty()) AbstractExporter.log.warn("no packets builded from flows");
			else this.exportPackets(packets);
		}
	}

	/**
	 * Export flows.
	 * 
	 * @see #export(Collection)
	 * @see org.dicr.netflow.packet.FlowListener#processFlows(java.util.Collection)
	 */
	public void processFlows(final Collection<? extends Flow> flows) {
		if (flows == null) throw new IllegalArgumentException("null flows");
		if (flows.isEmpty()) return;
		try {
			this.export(flows);
		} catch (final NetFlowException ex) {
			AbstractExporter.log.error("error exporting flows", ex);
		} catch (final IOException ex) {
			AbstractExporter.log.error("error exporting flows", ex);
		}

	}

	/**
	 * Export packet
	 * 
	 * @see NetFlowExporter#export(NetFlowPacket)
	 * @see org.dicr.netflow.packet.PacketListener#processPacket(org.dicr.netflow.packet.NetFlowPacket)
	 */
	public void processPacket(final NetFlowPacket packet) {
		if (packet == null) throw new IllegalArgumentException("null packet");
		try {
			this.export(packet);
		} catch (final NetFlowException ex) {
			AbstractExporter.log.error("error exporting packet", ex);
		} catch (final IOException ex) {
			AbstractExporter.log.error("error exporting packet", ex);
		}
	}
}
