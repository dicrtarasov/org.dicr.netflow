/**
 * NetFlowExporterTest.java 09.01.2007
 */
package org.dicr.netflow.test;

import java.net.*;
import java.util.*;

import org.dicr.netflow.cache.impl.*;
import org.dicr.netflow.exporter.impl.*;
import org.dicr.netflow.impl.v7.*;
import org.dicr.netflow.packet.*;
import org.dicr.traffic.source.*;
import org.dicr.util.net.*;

/**
 * NetFlow Cache & Exporter test
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070102
 */
public class NetFlowExporterTest {

	/**
	 * Test's main
	 * 
	 * @param args unused
	 * @throws Throwable
	 */
	@SuppressWarnings("boxing") public static void main(final String[] args) throws Throwable {
		// exporter
		final UDPExporter exporter = new UDPExporter();
		exporter.addAddress(new InetSocketAddress("127.0.0.1", 7654));

		// type
		final FlowType type = FlowTypeV7.INSTANCE;

		// cache
		final DefaultFlowCache cache = new DefaultFlowCache();
		cache.setFlowType(type);
		cache.addListener(exporter);
		cache.start();

		// generate traffic
		final Random r = new Random();
		while (true) {
			final TrafficElement el = new TrafficElement();
			el.setBytes(r.nextInt(1000));
			el.setDst(new IP(r.nextInt(3)));
			el.setDstAs(r.nextInt(3));
			el.setDstIf(r.nextInt(3));
			el.setDstMask(Mask.SINGLE);
			el.setDstPort(r.nextInt(3));
			el.setHop(new IP(r.nextInt(3)));
			el.setPackets((long) r.nextInt(1000));
			el.setProto(r.nextInt(3));
			el.setRouter(new IP(r.nextInt(3)));
			el.setSrc(new IP(r.nextInt(3)));
			el.setSrcAs(r.nextInt(3));
			el.setSrcIf(r.nextInt(3));
			el.setSrcMask(Mask.SINGLE);
			el.setSrcPort(r.nextInt(3));
			el.setTcpFlags((byte) r.nextInt(3));
			el.setTime(System.currentTimeMillis());
			el.setTos(Byte.valueOf((byte) r.nextInt(3)));
			final Flow flow = type.createFlow(el);
			cache.accumulate(flow);
			Thread.sleep(10);
		}
	}
}
