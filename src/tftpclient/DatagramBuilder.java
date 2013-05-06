/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tftpclient;

/**
 *
 * @author tom
 */
public class DatagramBuilder {

	static public byte[] requestPacket(Protocol.OpCode opCode, String filename, Protocol.Mode mode) {
		//TODO
		return new byte[1];
	}

	static public byte[] dataPacket(Protocol.OpCode opCode, int blockNumber, String data) {
		//TODO
		return new byte[1];
	}

	static public byte[] acknowledgmentPacket(int blockNumber) {
		//TODO
		return new byte[1];
	}

	static public byte[] errorPacket(Protocol.ErrCode errCode, String errMsg) {
		//TODO
		return new byte[1];
	}
}
