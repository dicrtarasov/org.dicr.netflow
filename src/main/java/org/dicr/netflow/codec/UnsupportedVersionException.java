/**
 * UnsupportedVersionException.java 20.12.2006
 */
package org.dicr.netflow.codec;

/**
 * Unsupported NetFlow version Exception
 * 
 * @author <A href='http://dicr.org'>Igor A Tarasov</A>
 * @version 061220
 */
public class UnsupportedVersionException extends CodecException {
	private static final long serialVersionUID = 8097535740193943580L;

	/**
     * Constructor
     */
	public UnsupportedVersionException() {
		super();
	}

	/**
     * Constructor
     * 
     * @param message
     */
	public UnsupportedVersionException(String message) {
		super(message);
	}

	/**
     * Constructor
     * 
     * @param message
     * @param cause
     */
	public UnsupportedVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * Constructor
     * 
     * @param cause
     */
	public UnsupportedVersionException(Throwable cause) {
		super(cause);
	}
}
