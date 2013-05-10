package model;

import java.nio.ByteBuffer;

/**
 *
 * @author tom
 */
public class AcknowledgmentPacket extends Packet {

	protected short _blockNumber;

	public AcknowledgmentPacket(short blockNumber) {
		this(Protocol.OpCode.ACK, blockNumber);
	}

	protected AcknowledgmentPacket(Protocol.OpCode opCode, short blockNumber) {
		super(opCode);
		this._blockNumber = blockNumber;
	}

	@Override
	public byte[] getBytes() {
		/*StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(this._blockNumber, Protocol.BLOCKNUM_SIZE));
		return packet.toString().getBytes();*/
		
		byte[] opCode = this.getOpCodeToByteArray();
		byte[] blockNumber = this.getBlockNumberToByteArray();
		
		ByteBuffer packet = ByteBuffer.allocate(opCode.length + blockNumber.length);
		
		packet.put(opCode);
		packet.put(blockNumber);
		
		/*System.arraycopy(opCode,		0, packet, 0,				opCode.length);
		System.arraycopy(blockNumber,	0, packet, opCode.length,	blockNumber.length);*/
		
		return packet.array();
	}

	public byte[] getBlockNumberToByteArray() {
		
		return ByteBuffer.allocate(2).putShort(_blockNumber).array();
	}
	
	public short getBlockNumber() {
		return _blockNumber;
	}

	public void setBlockNumber(short blockNumber) {
		this._blockNumber = blockNumber;
	}
	
	/*public static short getBlockNumber(byte b1, byte b2) {
		
		return ByteBuffer.allocate(2).putShort(b1).putShort(b2).getShort();
	}*/
}
