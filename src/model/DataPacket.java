package model;

import java.nio.ByteBuffer;

/**
 *
 * @author tom
 */
public class DataPacket extends AcknowledgmentPacket {

	protected byte[] _data;

	public DataPacket(short blockNumber, byte[] data) {
		super(Protocol.OpCode.DATA, blockNumber);
		this._data = data;
	}

	@Override
	public byte[] getBytes() {
		/*StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(this._blockNumber, Protocol.BLOCKNUM_SIZE));
		
		for(int i = 0; i < this._data.length; ++i) {
			packet.append((char)this._data[i]);
		}
		return packet.toString().getBytes();*/
		
		byte[] opCodeAndBlockNumber = super.getBytes();
		
		/*ByteBuffer bufferOpCode = ByteBuffer.allocate(2);
		bufferOpCode.putShort((short)this._opCode.v());
		
		ByteBuffer bufferBlockNumber = ByteBuffer.allocate(2);
		bufferBlockNumber.putShort((short)this._blockNumber);*/
		
		ByteBuffer packet = ByteBuffer.allocate(opCodeAndBlockNumber.length + _data.length);
		packet.put(opCodeAndBlockNumber);
		packet.put(_data);
		
		/*System.arraycopy(opCodeAndBlockNumber,	0, packet, 0,							opCodeAndBlockNumber.length);
		System.arraycopy(_data,					0, packet, opCodeAndBlockNumber.length, _data.length);*/
		
		return packet.array();
	}

	public byte[] getData() {
		System.out.println("getData (data.length) :" + _data.length);
		return _data;
	}

	public void setData(byte[] data) {
		this._data = data;
	}
}
