/**
 * UDPNetFlowCollectorMBean.java 08.01.2007
 */
package org.dicr.netflow.collector.impl;

/**
 * MBean interface of UDP NetFlow Collector.
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public interface UDPNetFlowCollectorMBean {

	/**
     * Set USP port number
     *
     * @param aPort port number to listen and receive packets.
     */
	public void setPort(int aPort);

	/**
     * Return listen port.
     *
     * @return UDP port number
     */
	public int getPort();

	/**
     * Check if listener thread is running
     *
     * @return true if listener thread runnign and receiving UDP packets
     */
	public boolean isRunning();

	/**
     * Start port listener thread.
     * <P>
     * This method start separate thread to listen specified UDP port and receive packet.
     * </P>
     */
	public void start();

	/**
     * Stop listener thread.
     */
	public void stop();

}
