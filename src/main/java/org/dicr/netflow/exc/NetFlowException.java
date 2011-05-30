package org.dicr.netflow.exc;

/**
 * NetFlowException
 * 
 * @author Igor A Tarasov &lt;java@dicr.org&gt;
 * @version 060123
 */
public class NetFlowException extends Exception {
	private static final long serialVersionUID = -677516316121435899L;

	/**
	 * Constructor
	 */
	public NetFlowException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public NetFlowException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public NetFlowException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 */
	public NetFlowException(final Throwable cause) {
		super(cause);
	}
}
