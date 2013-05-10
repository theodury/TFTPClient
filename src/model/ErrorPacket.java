package model;

import java.nio.ByteBuffer;

/**
 *
 * @author tom
 */
public class ErrorPacket extends Packet {

	protected Protocol.ErrCode _errCode;
	protected String _errMsg;

	public ErrorPacket(Protocol.ErrCode errCode, String errMsg) {
		super(Protocol.OpCode.ERR);
		this._errCode = errCode;
		this._errMsg = errMsg;
	}

	@Override
	public byte[] getBytes() {
		/*StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(this._errCode.v(), Protocol.ERRCODE_SIZE));
		packet.append(this._errMsg);
		packet.append(intToEscStr(0, 1));*/
		
		byte[] opCode = this.getOpCodeToByteArray();
		byte[] errCode = this.getErrCodeToByteArray();
		byte[] message = _errMsg.getBytes();
		
		ByteBuffer packet = ByteBuffer.allocate(opCode.length + errCode.length + message.length + 1);
		packet.put(opCode);
		packet.put(errCode);
		packet.put(message);
		packet.put((byte)0);
		
		return packet.array();
	}

	public byte[] getErrCodeToByteArray() {
		
		return ByteBuffer.allocate(2).putShort(_errCode.v()).array();
	}
	
	public Protocol.ErrCode getErrCode() {
		return _errCode;
	}

	public void setErrCode(Protocol.ErrCode errCode) {
		this._errCode = errCode;
	}

	public String getErrMsg() {
		return _errMsg;
	}

	public void setErrMsg(String errMsg) {
		this._errMsg = errMsg;
	}
}
