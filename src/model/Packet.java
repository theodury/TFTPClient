package model;

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

	protected String intToEscStr(int value, int bytesCount) {
		String format = "%" + new Integer(bytesCount).toString() + "d";
		String valueStr = String.format(format, value);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytesCount; ++i) {
			sb.append(Integer.valueOf(valueStr.charAt(i)));
		}
		return sb.toString();
	}

	public Protocol.OpCode getOpCode() {
		return _opCode;
	}

	public void setOpCode(Protocol.OpCode opCode) {
		this._opCode = opCode;
	}
}
