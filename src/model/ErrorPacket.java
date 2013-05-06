package model;

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
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(this._errCode.v(), Protocol.ERRCODE_SIZE));
		packet.append(this._errMsg);
		packet.append(intToEscStr(0, 1));
		return packet.toString().getBytes();
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
