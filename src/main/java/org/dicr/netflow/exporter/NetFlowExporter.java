/**
 * NetFlowExporter.java 08.01.2007
 */
package org.dicr.netflow.exporter;

import java.io.*;
import java.util.*;

import org.dicr.netflow.codec.*;
import org.dicr.netflow.exc.*;
import org.dicr.netflow.packet.*;

/**
 * NetFlow Exporter.
 * <P>
 * Export NetFlow Packets.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public interface NetFlowExporter extends PacketListener, FlowListener {
	/**
	 * Export packet.
	 * <P>
	 * Export packet to destination in implementation specific way.
	 * </P>
	 * 
	 * @param packet packet to export
	 * @throws IOException channel error while exporting
	 * @throws CodecException error encoding packet for export
	 */
	public void export(NetFlowPacket packet) throws IOException, CodecException;

	/**
	 * Build packet from flows and export it to implementation specific destination.
	 * 
	 * @param flows flow to export
	 * @throws IOException I/O error while exporting
	 * @throws NetFlowException error while building and sending packet
	 */
	public void export(Collection<? extends Flow> flows) throws IOException, NetFlowException;
}
