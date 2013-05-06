package model;

/**
 *
 * @author tom
 */
public class DataPacket extends AcknowledgmentPacket {

	protected String _data;

	public DataPacket(int blockNumber, String data) {
		super(Protocol.OpCode.DATA, blockNumber);
		this._data = data;
	}

	@Override
	public byte[] getBytes() {
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(this._blockNumber, Protocol.BLOCKNUM_SIZE));
		packet.append(this._data);
		return packet.toString().getBytes();
	}

	public String getData() {
		return _data;
	}

	public void setData(String data) {
		this._data = data;
	}
}
