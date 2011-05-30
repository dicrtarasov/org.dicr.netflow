/**
 * UDPNetFlowCollector.java 12.07.2006
 */
package org.dicr.netflow.collector.impl;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

import javax.management.*;

import org.apache.log4j.*;
import org.dicr.netflow.codec.*;
import org.dicr.netflow.packet.*;

/**
 * UDP NetFlow Collector.
 * <P>
 * This implementation provide separate thread to receive and decode packets from specified UDP port. For network
 * operations it use NIO API.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060712
 */
public class UDPNetFlowCollector extends AbstractNetFlowCollector implements MBeanRegistration, UDPNetFlowCollectorMBean {
	/** Logger */
	private static final Logger log = Logger.getLogger(UDPNetFlowCollector.class);

	/** Default object name for MBean */
	public static final String DEFAULT_OBJECT_NAME = "org.dicr:service=traffic;type=source;name=netflow";

	/** Port to listen */
	private int port = -1;

	/** Client IO thread */
	private ClientThread clientThread = null;

	/**
	 * Constructor
	 */
	public UDPNetFlowCollector() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param aPort port to listen
	 */
	public UDPNetFlowCollector(final int aPort) {
		super();
		this.setPort(aPort);
	}

	/**
	 * Set port to listen.
	 * 
	 * @see org.dicr.netflow.collector.impl.UDPNetFlowCollectorMBean#setPort(int)
	 */
	public void setPort(final int aPort) {
		if (aPort < 0 || aPort > 65535) throw new IllegalArgumentException("listenPort: " + aPort);
		synchronized (this) {
			this.port = aPort;
		}
		UDPNetFlowCollector.log.debug("configured port: " + aPort);
	}

	/**
	 * Return listen port
	 * 
	 * @see org.dicr.netflow.collector.impl.UDPNetFlowCollectorMBean#getPort()
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @see org.dicr.netflow.collector.impl.UDPNetFlowCollectorMBean#isRunning()
	 */
	public boolean isRunning() {
		synchronized (this) {
			return this.clientThread != null && this.clientThread.isAlive();
		}
	}

	/**
	 * @see org.dicr.netflow.collector.impl.UDPNetFlowCollectorMBean#start()
	 */
	public void start() {
		synchronized (this) {
			if (this.port < 0) throw new IllegalStateException("port not configured");
			if (this.isRunning()) UDPNetFlowCollector.log.debug("NetFlow client already running on port: " + this.port);
			else {
				this.clientThread = new ClientThread(this.port, UDPNetFlowCollector.log);
				this.clientThread.start();
			}
		}
	}

	/**
	 * @see org.dicr.netflow.collector.impl.UDPNetFlowCollectorMBean#stop()
	 */
	public void stop() {
		synchronized (this) {
			if (!this.isRunning()) UDPNetFlowCollector.log.debug("NetFlow client on port " + this.port
			        + " already stopped");
			else {
				this.clientThread.interrupt();
				this.clientThread = null;
			}
		}
	}

	/**
	 * NetFLow client IO thread
	 */
	protected class ClientThread extends Thread {
		/** port to listen */
		private int listenPort = 0;

		/** Lopgger */
		private Logger loger = null;

		/**
		 * Constructor.
		 * 
		 * @param aPort port to listen
		 * @param alog logger
		 */
		protected ClientThread(final int aPort, final Logger alog) {
			super();
			if (aPort < 0 || aPort > 65535) throw new IllegalArgumentException("port: " + aPort);
			this.listenPort = aPort;
			if (alog == null) throw new IllegalArgumentException("null log");
			this.loger = alog;
			this.setName("NetFlow client on port " + aPort);
			this.setDaemon(false);
		}

		/**
		 * Run client. Read and fire packets.
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			this.loger.info("starting UDP NetFlow client on port: " + this.listenPort);
			final ByteBuffer buf = ByteBuffer.allocate(65535);
			DatagramChannel channel = null;
			Selector sel = null;
			try {
				// prepare channel
				channel = DatagramChannel.open();
				channel.configureBlocking(false);
				channel.socket().setSendBufferSize(65535);
				channel.socket().bind(new InetSocketAddress("0.0.0.0", this.listenPort));
				sel = Selector.open();
				channel.register(sel, SelectionKey.OP_READ);
				// read data until interrupted
				while (!this.isInterrupted()) {
					try {
						// wait for packet
						if (sel.select() < 1) continue;
						final Set<SelectionKey> keys = sel.selectedKeys();
						final Iterator<SelectionKey> iterator = keys.iterator();
						// handle keys
						while (iterator.hasNext()) {
							final SelectionKey key = iterator.next();
							if (!key.isReadable()) continue;
							iterator.remove();
							// read data
							buf.clear();
							final SocketAddress addr = channel.receive(buf);
							// decode packet
							buf.flip();
							final NetFlowPacket packet = NetFlowCodec.decode(buf);
							this.loger.trace("received " + packet.getFlowsCount() + " flows in " + packet.getFlowType()
							        + " packet from address " + addr + " on port " + this.listenPort);
							// fire packet
							UDPNetFlowCollector.this.firePacket(packet);
						}
					} catch (final ClosedByInterruptException ex) {
						this.loger.debug("interrupted USP NetFlow client on port " + this.listenPort);
						break;
					} catch (final Exception ex) {
						this.loger.error("error receiving NetFlow packet", ex);
					}
				}
			} catch (final IOException ex) {
				this.loger.error("error opening datagram channel on port " + this.listenPort, ex);
			} finally {
				// close channel
				if (sel != null) try {
					sel.close();
				} catch (final Exception ex) {
					// NOP
				}
				if (channel != null) try {
					channel.close();
				} catch (final Exception ex) {
					// NOP
				}
			}
			this.loger.info("stopped UDP NetFlow client at port " + this.listenPort);
		}
	}

	/**
	 * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer, javax.management.ObjectName)
	 */
	public ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
		return name != null ? name : ObjectName.getInstance(UDPNetFlowCollector.DEFAULT_OBJECT_NAME);
	}

	/**
	 * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
	 */
	public void postRegister(final Boolean registrationDone) {
		if (registrationDone != null && registrationDone.booleanValue() && !this.isRunning()) this.start();
	}

	/**
	 * @see javax.management.MBeanRegistration#preDeregister()
	 */
	public void preDeregister() throws Exception {
		if (this.isRunning()) this.stop();
	}

	/**
	 * @see javax.management.MBeanRegistration#postDeregister()
	 */
	public void postDeregister() {
	// NOP
	}

}
