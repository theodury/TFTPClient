/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Pierre
 */
public class TFTPErrorException extends Exception {

	private Protocol.ErrCode _errCode;
	
	/**
	 * Creates a new instance of
	 * <code>TFTPErrorException</code> without detail message.
	 */
	public TFTPErrorException() {
	}

	/**
	 * Constructs an instance of
	 * <code>TFTPErrorException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public TFTPErrorException(Protocol.ErrCode errCode, String msg) {
		super(msg);
		this._errCode = errCode;
	}
	
	public Protocol.ErrCode getErrCode() {
		return _errCode;
	}
}
