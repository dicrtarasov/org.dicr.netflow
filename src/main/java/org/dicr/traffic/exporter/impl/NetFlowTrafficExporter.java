/**
 * NetFlowTrafficExporter.java 08.01.2007
 */
package org.dicr.traffic.exporter.impl;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.exporter.*;
import org.dicr.netflow.packet.*;
import org.dicr.traffic.exporter.*;
import org.dicr.traffic.source.*;

/**
 * NetFlow Traffic Exporter.
 * <P>
 * Export {@link TrafficElement traffic} to {@link NetFlowPacket NetFlow} protocol.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public class NetFlowTrafficExporter implements TrafficExporter {
	/** Logger */
	private static final Logger log = Logger.getLogger(NetFlowTrafficExporter.class);

	/** NetFlowExporter */
	private NetFlowExporter exporter = null;

	/** Flow Type */
	private FlowType type = null;

	/**
	 * Constructor
	 */
	public NetFlowTrafficExporter() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param flowExporter exporter to use
	 * @param flowType NetFlow type to export
	 * @see #setExporter(NetFlowExporter)
	 */
	public NetFlowTrafficExporter(final NetFlowExporter flowExporter, final FlowType flowType) {
		super();
		this.setExporter(flowExporter);
		this.setType(flowType);
	}

	/**
	 * Set exporter
	 * 
	 * @param flowExporter exporter to use
	 */
	public void setExporter(final NetFlowExporter flowExporter) {
		if (flowExporter == null) throw new IllegalArgumentException("null exporter");
		synchronized (this) {
			this.exporter = flowExporter;
		}
		NetFlowTrafficExporter.log.debug("configured exporter: " + flowExporter);
	}

	/**
	 * Return exporter
	 * 
	 * @return NetFlow exporter
	 */
	public NetFlowExporter getExporter() {
		synchronized (this) {
			return this.exporter;
		}
	}

	/**
	 * Set type
	 * 
	 * @param flowType NetFlow type to export to
	 */
	public void setType(final FlowType flowType) {
		if (flowType == null) throw new IllegalArgumentException("null flow type");
		synchronized (this) {
			this.type = flowType;
		}
	}

	/**
	 * Return type
	 * 
	 * @return export NetFlow type
	 */
	public FlowType getType() {
		synchronized (this) {
			return this.type;
		}
	}

	/**
	 * @see org.dicr.traffic.source.TrafficListener#processTrafficEvent(org.dicr.traffic.source.TrafficEvent)
	 */
	public void processTrafficEvent(final TrafficEvent e) {
		if (e == null) throw new IllegalArgumentException("null traffic event");
		if (e.getTraffic().isEmpty()) {
			NetFlowTrafficExporter.log.debug("no traffic to export");
			return;
		}
		synchronized (this) {
			// check initialization
			if (this.exporter == null) throw new IllegalStateException("exporter not configured");
			if (this.type == null) throw new IllegalArgumentException("type not configured");
			// convert traffic to flows
			final Collection<Flow> flows = new ArrayList<Flow>();
			for (final TrafficElement element : e.getTraffic()) {
				flows.add(this.type.createFlow(element));
			}
			// export flows
			try {
				this.exporter.export(flows);
			} catch (final NetFlowException ex) {
				NetFlowTrafficExporter.log.error("error exporting traffic", ex);
			} catch (final IOException ex) {
				NetFlowTrafficExporter.log.error("error exporting traffic", ex);
			}
		}
	}
}
