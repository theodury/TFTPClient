/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author tom
 */
public class Protocol {

	final static public int PORT = 69;
	final static public int BUFFER_SIZE = 514;
	final static public int OPCODE_SIZE = 2;
	final static public int ERRCODE_SIZE = 2;
	final static public int BLOCKNUM_SIZE = 2;

	static public enum OpCode {

		RRQ(1), // Read request
		WRQ(2), // Write request
		DATA(3), // Data
		ACK(4), // Acknoledgment
		ERR(5); // Error
		private int val;

		OpCode(int val) {
			this.val = val;
		}

		public int v() {
			return val;
		}
	};

	static public enum Mode {

		NETASCII,
		OCTET,
		MAIL;

		public String v() {
			return this.toString();
		}
	}

	static public enum ErrCode {

		UNDEFINED(0), // Not defined, see error message
		NOTFOUND(1), // File not found
		NOACCESS(2), // Access violation
		DISKFULL(3), // Disk full or allocation exceeded
		OPERATION(4), // Illegal TFTP operation
		TRANSFERID(5), // Unknown transfer ID
		FILEEXISTS(6), // File already exists
		NOUSER(7); // No such user
		private int val;

		ErrCode(int val) {
			this.val = val;
		}

		public int v() {
			return val;
		}
	};
}
