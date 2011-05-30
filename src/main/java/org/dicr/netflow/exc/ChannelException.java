/**
 * ChannelException.java 08.01.2007
 */
package org.dicr.netflow.exc;

/**
 * NetFlow Channel Exception
 *
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 070108
 */
public class ChannelException extends NetFlowException {
	/** Serial ID */
	private static final long serialVersionUID = -3584480754452748939L;

	/**
     * Constructor
     */
	public ChannelException() {
		super();
	}

	/**
     * Constructor
     *
     * @param message error message
     */
	public ChannelException(String message) {
		super(message);
	}

	/**
     * Constructor
     *
     * @param message error message
     * @param cause error reason
     */
	public ChannelException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     *
     * @param cause error reason
     */
	public ChannelException(Throwable cause) {
		super(cause);
	}
}
