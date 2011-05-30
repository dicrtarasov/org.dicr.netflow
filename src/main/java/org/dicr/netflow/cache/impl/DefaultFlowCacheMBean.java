/**
 * DefaultFlowCacheMBean.java 20.09.2007
 */
package org.dicr.netflow.cache.impl;

import java.util.*;

import org.dicr.netflow.packet.*;

/**
 * MBean interface for DefaultCache
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070920
 */
public interface DefaultFlowCacheMBean {

	/**
	 * Set buffer size
	 * 
	 * @param size buffer size in elements
	 */
	public void setBufferSize(final int size);

	/**
	 * Return buffer size
	 * 
	 * @return buffer size in elements
	 */
	public int getBufferSize();

	/**
	 * Set expire time
	 * 
	 * @param time expire time in seconds
	 */
	public void setExpireTime(final int time);

	/**
	 * Return expire time
	 * 
	 * @return flows expire time
	 */
	public int getExpireTime();

	/**
	 * Accumulate flow
	 * 
	 * @param flow flow to accumulate
	 */
	public void accumulate(final Flow flow);

	/**
	 * Return cache content
	 * 
	 * @return flows from cache
	 */
	public Collection<Flow> getContent();

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#clear()
	 */
	public void clear();

	/**
	 * Expire cache.
	 * 
	 * @return expired flows
	 */
	public Collection<Flow> expire();

	/**
	 * Check if expiration thread is running
	 * 
	 * @return true if running
	 */
	public boolean isRunning();

	/**
	 * Start expiration thread
	 */
	public void start();

	/**
	 * Stop expiration thread
	 */
	public void stop();

}
