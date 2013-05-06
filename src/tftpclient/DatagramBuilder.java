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
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(filename);
		packet.append(intToEscStr(0, 1));
		packet.append(mode.v());
		packet.append(intToEscStr(0, 1));
		return packet.toString().getBytes();
	}

	static public byte[] dataPacket(Protocol.OpCode opCode, int blockNumber, String data) {
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(blockNumber, Protocol.BLOCKNUM_SIZE));
		packet.append(data);
		return packet.toString().getBytes();
	}

	static public byte[] acknowledgmentPacket(int blockNumber) {
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(Protocol.OpCode.ACK.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(blockNumber, Protocol.BLOCKNUM_SIZE));
		return packet.toString().getBytes();
	}

	static public byte[] errorPacket(Protocol.ErrCode errCode, String errMsg) {
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(Protocol.OpCode.ERR.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(errCode.v(), Protocol.ERRCODE_SIZE));
		packet.append(errMsg);
		packet.append(intToEscStr(0, 1));
		return packet.toString().getBytes();
	}

	static private String intToEscStr(int value, int bytesCount) {
		String format = "%" + new Integer(bytesCount).toString() + "d";
		String valueStr = String.format(format, value);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytesCount; ++i) {
			sb.append("\\").append(valueStr.charAt(i));
		}
		return sb.toString();
	}
}
