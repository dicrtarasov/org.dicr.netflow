/**
 * CodecException.java 20.12.2006
 */
package org.dicr.netflow.codec;

import org.dicr.netflow.exc.*;

/**
 * NetFlowCodec Exception
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061219
 */
public class CodecException extends NetFlowException {
	private static final long serialVersionUID = 838261428620128837L;

	/**
	 * Constructor 
	 */
	public CodecException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public CodecException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 * @param cause
	 */
	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param cause
	 */
	public CodecException(Throwable cause) {
		super(cause);
	}
}
