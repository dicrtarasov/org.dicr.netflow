/**
 * FlowCache.java 28.11.2006
 */
package org.dicr.netflow.cache;

import java.util.*;

import org.dicr.netflow.packet.*;

/**
 * Flow Cache.
 * <P>
 * Accumulate in buffer flows of specific type. Identical flows {@link Flow#merge(Flow) merged} together, other added to buffer as is.
 * </P>
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061219
 */
public interface FlowCache {
	/**
     * Return type of flow
     *
     * @return type of flow, stored in the cache
     */
	public FlowType getFlowType();

	/**
     * Return buffer size
     *
     * @return number of flows, which can be stored in buffer
     */
	public int getBufferSize();

	/**
     * Set buffer size
     *
     * @param size number of flows, which can be stored in buffer
     */
	public void setBufferSize(int size);

	/**
     * Accumulate flow into the cache.
     * <P>
     * First, try to {@link Flow#merge(Flow) merge} flow which each other flow in buffer. If flow can't be merged, then simple
     * add it to buffer. Flow must be the same type as {@link #getFlowType() cache type}.
     * </P>
     *
     * @param flow flow to accumelate.
     * @throws IllegalArgumentException if flow is null or flow type is incorrect
     */
	public void accumulate(Flow flow) throws IllegalArgumentException;

	/**
     * Return cache content
     *
     * @return current content of cache buffer
     */
	public Collection<Flow> getContent();

	/**
     * Clear cache. Delete stored all flows.
     */
	public void clear();

	/**
     * Set flow expire time.
     * <P>
     * Flow expire time caclulated from flow creation time
     * </P>
     *
     * @param seconds time in seconds, after which flow considered to be expired.
     */
	public void setExpireTime(int seconds);

	/**
     * Return flow expire time
     *
     * @return current expire time of flows in seconds
     */
	public int getExpireTime();

	/**
     * Expire cache.
     * <P>
     * Inspect all flows in the cache and remove expired. Also remove flows if buffer overflow, until buffer content not
     * overflow {@link #getBufferSize()}.
     *
     * @return expired flows
     */
	public Collection<Flow> expire();
}
