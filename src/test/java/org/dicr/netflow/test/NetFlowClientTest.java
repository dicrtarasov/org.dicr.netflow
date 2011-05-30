/**
 * NetFlowClientTest.java 09.01.2007
 */
package org.dicr.netflow.test;

import org.dicr.netflow.collector.*;
import org.dicr.netflow.collector.impl.*;
import org.dicr.netflow.packet.*;

/**
 * NetFlow Client Test
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070109
 */
public class NetFlowClientTest implements PacketListener {
	private NetFlowCollector collector = null;

	/** Constructor */
	public NetFlowClientTest() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param flowClient NetFlow collector (collector) implementation to test
	 */
	public NetFlowClientTest(final NetFlowCollector flowClient) {
		super();
		this.setCollector(flowClient);
	}

	/**
	 * Set NetFlow collector
	 * 
	 * @param flowCollector NetFlow collector (collector) implementation to test
	 */
	public void setCollector(final NetFlowCollector flowCollector) {
		if (flowCollector == null) throw new IllegalArgumentException("null collector");
		synchronized (this) {
			if (this.collector != null) this.collector.removeListener(this);
			this.collector = flowCollector;
			this.collector.addListener(this);
		}
	}

	/**
	 * @see org.dicr.netflow.packet.PacketListener#processPacket(org.dicr.netflow.packet.NetFlowPacket)
	 */
	public void processPacket(final NetFlowPacket packet) {
		if (packet == null) throw new IllegalArgumentException("null packet");
		System.out.println(packet);
	}

	/**
	 * Test Main
	 * 
	 * @param args unused
	 */
	public static void main(final String[] args) {
		// register flow types
		// FlowTypeV1.INSTANCE.getClass();
		// FlowTypeV5.INSTANCE.getClass();
		// FlowTypeV6.INSTANCE.getClass();
		// FlowTypeV7.INSTANCE.getClass();
		// FlowTypeV8.AS.getClass();

		// collector
		final UDPNetFlowCollector client = new UDPNetFlowCollector();
		client.setPort(7654);
		client.start();
		new NetFlowClientTest(client);
	}
}
