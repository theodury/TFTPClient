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
	final static public int DATA_SIZE = 512;
	final static public int OPCODE_SIZE = 2;
	final static public int ERRCODE_SIZE = 2;
	final static public int BLOCKNUM_SIZE = 2;

	static public enum OpCode {

		RRQ((short)1), // Read request
		WRQ((short)2), // Write request
		DATA((short)3), // Data
		ACK((short)4), // Acknoledgment
		ERR((short)5); // Error
		private short val;

		OpCode(short val) {
			this.val = val;
		}

		public short v() {
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

		UNDEFINED((short)0), // Not defined, see error message
		NOTFOUND((short)1), // File not found
		NOACCESS((short)2), // Access violation
		DISKFULL((short)3), // Disk full or allocation exceeded
		OPERATION((short)4), // Illegal TFTP operation
		TRANSFERID((short)5), // Unknown transfer ID
		FILEEXISTS((short)6), // File already exists
		NOUSER((short)7); // No such user
		private short val;

		ErrCode(short val) {
			this.val = val;
		}

		public short v() {
			return val;
		}
	};
}
