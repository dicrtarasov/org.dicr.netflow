/**
 * DefaultFlowCache.java 07.07.2006
 */
package org.dicr.netflow.cache.impl;

import java.util.*;

import org.apache.log4j.*;
import org.dicr.netflow.packet.*;

/**
 * Default Flows Cache.
 * <P>
 * This is a default implementation of flow cache. This implementation provide thread, which automatically call
 * {@link #expire() expire} every second and fire expired flows to
 * {@link AbstractFlowCache#setFlowListeners(Set) listeners}.
 * </P>
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 060707
 */
public class DefaultFlowCache extends AbstractFlowCache implements DefaultFlowCacheMBean {
	/** Logger */
	private static final Logger log = Logger.getLogger(DefaultFlowCache.class);

	/** Buffer size */
	private int bufferSize = 1000;

	/** Cache buffer, list is match faster then set */
	private final Collection<Flow> buffer = new ArrayList<Flow>(this.bufferSize);

	/** Flows expire time in milliseconds */
	private long expireTime = 60000;

	/** Expiration thread */
	private ExpireThread expireThread = null;

	/**
	 * Default constructor. Used in IoC configurations.
	 */
	public DefaultFlowCache() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param type type of flows in the cache.
	 */
	public DefaultFlowCache(final FlowType type) {
		super();
		this.setFlowType(type);
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#setBufferSize(int)
	 */
	public void setBufferSize(final int size) {
		if (size < 1) throw new IllegalArgumentException("buffer size: " + size);
		synchronized (this.buffer) {
			this.bufferSize = size;
		}
		DefaultFlowCache.log.debug("configured buffer size: " + size);
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#getBufferSize()
	 */
	public int getBufferSize() {
		synchronized (this.buffer) {
			return this.bufferSize;
		}
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#setExpireTime(int)
	 */
	public void setExpireTime(final int time) {
		if (time < 1) throw new IllegalArgumentException("time: " + time);
		synchronized (this.buffer) {
			this.expireTime = time * 1000;
		}
		DefaultFlowCache.log.debug("configured expire time: " + time + " seconds");
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#getExpireTime()
	 */
	public int getExpireTime() {
		synchronized (this.buffer) {
			return (int) (this.expireTime / 1000);
		}
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#accumulate(org.dicr.netflow.packet.Flow)
	 */
	public void accumulate(final Flow flow) {
		if (flow == null) throw new IllegalArgumentException("null flow to accumulate");
		// check flow type
		final FlowType type = this.getFlowType();
		if (type == null) throw new IllegalStateException("flow type not initialized");
		if (!type.equals(flow.getFlowType())) throw new IllegalArgumentException("illegal flow type to accumulate: "
		        + flow.getFlowType());
		// aggregate
		synchronized (this.buffer) {
			boolean aggregated = false;
			for (final Flow cflow : this.buffer) {
				if (cflow.merge(flow)) {
					aggregated = true;
					break;
				}
			}
			// add if not aggregated
			if (!aggregated) this.buffer.add(flow);
		}
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#getContent()
	 */
	public Collection<Flow> getContent() {
		synchronized (this.buffer) {
			return Collections.unmodifiableCollection(this.buffer);
		}
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#clear()
	 */
	public void clear() {
		synchronized (this.buffer) {
			this.buffer.clear();
		}
		DefaultFlowCache.log.trace("content cleared");
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#expire()
	 */
	public Collection<Flow> expire() {
		final Set<Flow> expiredFlows = new HashSet<Flow>();
		final long uptime = System.currentTimeMillis() - Flow.bootTime;
		synchronized (this.buffer) {
			// expire by time
			Iterator<? extends Flow> bufferIterator = this.buffer.iterator();
			while (bufferIterator.hasNext()) {
				final Flow flow = bufferIterator.next();
				if (uptime - flow.getFirst() >= this.expireTime) {
					expiredFlows.add(flow);
					bufferIterator.remove();
				}
			}
			// expire by size
			final int currSize = this.buffer.size();
			if (currSize > this.bufferSize) {
				DefaultFlowCache.log.warn("buffer overflow by " + (currSize - this.bufferSize) + " flows - expiring");
				bufferIterator = this.buffer.iterator();
				for (int i = 0; i < currSize - this.bufferSize; i++) {
					expiredFlows.add(bufferIterator.next());
					bufferIterator.remove();
				}
			}
		}
		return expiredFlows;
	}

	/**
	 * Do cache expiration and fire expired flows to listeners.
	 */
	protected void doExpiration() {
		final Collection<Flow> expiredFlows = this.expire();
		if (!expiredFlows.isEmpty()) this.fireFlows(expiredFlows);
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#isRunning()
	 */
	public boolean isRunning() {
		synchronized (this) {
			return this.expireThread != null && this.expireThread.isAlive();
		}
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#start()
	 */
	public void start() {
		if (this.getFlowType() == null) throw new IllegalStateException("flow type not initialized");
		synchronized (this) {
			if (this.isRunning()) DefaultFlowCache.log.debug("flow cache expiration thread is already running");
			else {
				this.expireThread = new ExpireThread(DefaultFlowCache.log);
				this.expireThread.start();
			}
		}
	}

	/**
	 * @see org.dicr.netflow.cache.impl.DefaultFlowCacheMBean#stop()
	 */
	public void stop() {
		synchronized (this) {
			if (!this.isRunning()) DefaultFlowCache.log.debug("flow cache expiration thread is already stopped");
			else {
				this.expireThread.interrupt();
				this.expireThread = null;
			}
		}
	}

	/**
	 * Cache Expiration Thread
	 */
	protected class ExpireThread extends Thread {
		/** Logger */
		private Logger threadLog = null;

		/**
		 * Constructor
		 * 
		 * @param theLog logger to log messages
		 */
		protected ExpireThread(final Logger theLog) {
			super("FlowCache expiration");
			if (theLog == null) throw new IllegalArgumentException("null threadLog");
			this.threadLog = theLog;
			this.setDaemon(false);
		}

		/** Run cache flows expiration and export. */
		@Override
		public final void run() {
			this.threadLog.info("starting FlowCache expiration thread, do expiration every 1 second");
			while (!this.isInterrupted()) {
				try {
					Thread.sleep(1000);
					DefaultFlowCache.this.doExpiration();
					Thread.yield();
				} catch (final InterruptedException ex) {
					this.threadLog.debug("FlowCache expiration thread interrupted");
				} catch (final Exception ex) {
					this.threadLog.error("error running flow cache expiration process", ex);
				}
			}
			this.threadLog.info("FlowCache expiration thread stopped");
		}
	}
}
