/**
 * UDPFlowExporter.java 07.07.2006
 */
package org.dicr.netflow.exporter.impl;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.codec.*;
import org.dicr.netflow.packet.*;

/**
 * UDP NetFlow Exporter.
 * <P>
 * Export NetFlow Packets throw UDP network socket.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060710
 */
public class UDPExporter extends AbstractExporter {
	/** Logger */
	private static final Logger log = Logger.getLogger(UDPExporter.class);

	/** Addresses to export */
	private final Set<SocketAddress> addresses = new HashSet<SocketAddress>();

	/** Datagram channel */
	private DatagramChannel channel = null;

	/** Byte Buffer */
	private final ByteBuffer buf = ByteBuffer.allocate(65535);

	/**
	 * Constructor.
	 */
	public UDPExporter() {
		super();
	}

	/**
	 * Set destinations addresses
	 * 
	 * @param exportAddresses addresses to export flows
	 */
	public void setAddresses(final Set<SocketAddress> exportAddresses) {
		if (exportAddresses == null) throw new IllegalArgumentException("null addresses to export");
		synchronized (this.addresses) {
			this.addresses.clear();
			this.addresses.addAll(exportAddresses);
		}
		UDPExporter.log.debug("configured " + exportAddresses.size() + " addresses to export flows to");
	}

	/**
	 * Set destinations addresses
	 * 
	 * @param exportAddresses addreses to export, where key is host addrerss and value is port
	 */
	public void setAddresses(final Map<String, Integer> exportAddresses) {
		if (exportAddresses == null) throw new IllegalArgumentException("null addresses");
		synchronized (this.addresses) {
			this.addresses.clear();
			for (final String addr : exportAddresses.keySet()) {
				if (addr == null || addr.isEmpty()) throw new IllegalArgumentException("empty address");
				final Integer port = exportAddresses.get(addr);
				if (port == null || port.intValue() < 0 || port.intValue() > 65535) throw new IllegalArgumentException(
				        "incorrect port in addresses: " + port);
				this.addresses.add(new InetSocketAddress(addr, port.intValue()));
			}
		}
		UDPExporter.log.debug("configured " + exportAddresses.size() + " addresses for export");
	}

	/**
	 * Add export destination address
	 * 
	 * @param addr destination address to add
	 */
	public void addAddress(final SocketAddress addr) {
		if (addr == null) throw new IllegalArgumentException("null address");
		synchronized (this) {
			this.addresses.add(addr);
		}
	}

	/**
	 * Return export destinations addresses
	 * 
	 * @return list of addresses to which flows exporting
	 */
	public Set<SocketAddress> getAddresses() {
		synchronized (this.addresses) {
			return Collections.unmodifiableSet(this.addresses);
		}
	}

	/**
	 * Open datagram channel.
	 * <P>
	 * Call to this method is optional and performed automatically in first call to {@link #export(NetFlowPacket)}
	 * </P>
	 * 
	 * @throws IOException error opening channel
	 */
	public void open() throws IOException {
		synchronized (this.buf) {
			if (this.channel == null) {
				this.channel = DatagramChannel.open();
				this.channel.configureBlocking(true);
				this.channel.socket().setReceiveBufferSize(65535);
				this.channel.socket().setReceiveBufferSize(65535);
			}
		}
	}

	/**
	 * Export flows. Export flows to configured addresses.
	 * 
	 * @param packet packet to export.
	 * @throws IOException channel IO error
	 * @throws CodecException packet encoding problem
	 */
	public void export(final NetFlowPacket packet) throws IOException, CodecException {
		if (packet == null) throw new IllegalArgumentException("null packet");

		// targets to export to
		final Set<SocketAddress> targets = this.getAddresses();
		if (targets.isEmpty()) {
			UDPExporter.log.trace("no addresses configured to export to");
			return;
		}

		synchronized (this.buf) {
			UDPExporter.log.trace("exporting " + packet.getFlowsCount() + " flows in " + packet.getFlowType()
			        + " packet to " + targets.size() + " addresses");

			// configure channel
			if (this.channel == null) this.open();

			// encode packet
			this.buf.clear();
			packet.getFlowType().getCodec().encodePacket(packet, this.buf);
			this.buf.flip();

			// send to all addresses (throws IOException)
			for (final SocketAddress addr : targets) {
				this.channel.send(this.buf, addr);
				this.buf.rewind();
			}
		}
	}

	/**
	 * Close datagram channel.
	 */
	public void close() {
		synchronized (this.buf) {
			if (this.channel != null) try {
				this.channel.close();
			} catch (final Exception ex) {
				// NOP
			}
		}
		UDPExporter.log.info("datagram channel closed");
	}

	/**
	 * Finalize object. Invoke {@link #close()}.
	 */
	@Override
	public void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
}
