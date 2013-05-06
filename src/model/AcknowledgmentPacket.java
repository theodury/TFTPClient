package model;

/**
 *
 * @author tom
 */
public class AcknowledgmentPacket extends Packet {

	protected int _blockNumber;

	public AcknowledgmentPacket(int blockNumber) {
		this(Protocol.OpCode.ACK, blockNumber);
	}

	protected AcknowledgmentPacket(Protocol.OpCode opCode, int blockNumber) {
		super(opCode);
		this._blockNumber = blockNumber;
	}

	@Override
	public byte[] getBytes() {
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(intToEscStr(this._blockNumber, Protocol.BLOCKNUM_SIZE));
		return packet.toString().getBytes();
	}

	public int getBlockNumber() {
		return _blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this._blockNumber = blockNumber;
	}
}
