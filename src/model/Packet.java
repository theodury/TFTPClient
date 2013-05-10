package model;

import java.nio.ByteBuffer;

/**
 *
 * @author tom
 */
abstract public class Packet {

	protected Protocol.OpCode _opCode;

	public Packet(Protocol.OpCode opCode) {
		this._opCode = opCode;
	}

	abstract public byte[] getBytes();

	/*protected byte[] intToByteArray(int value, int bytesCount) {
		
	}*/
	
	protected String intToEscStr(int value, int bytesCount) {
		/*String format = "%" + new Integer(bytesCount).toString() + "d";
		String valueStr = String.format(format, value);
		System.out.println(valueStr);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytesCount; ++i) {
			
			sb.append(Integer.valueOf(valueStr.charAt(i)));
		*/
		String valStr = Integer.toString(value);
		StringBuilder escStr = new StringBuilder();
		
		/*for(int i = 0; i < bytesCount - valStr.length(); ++i) {
			
			escStr.append((char)0);
		}*/
		for(int i = 0; /*i < valStr.length() &&*/ i < bytesCount; ++i) {
			
			char b = (char)(value % 256);
			escStr.insert(0,b);
			value = value/256;
		}
		
		return escStr.toString();
	}

	public byte[] getOpCodeToByteArray() {
		
		return ByteBuffer.allocate(2).putShort(_opCode.v()).array();
	}
	
	public Protocol.OpCode getOpCode() {
		return _opCode;
	}

	public void setOpCode(Protocol.OpCode opCode) {
		this._opCode = opCode;
	}
	
	public static short getCode(byte b1, byte b2) {
		
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.put(b1);
		buffer.put(b2);
		
		return buffer.getShort(0);
	}
}
