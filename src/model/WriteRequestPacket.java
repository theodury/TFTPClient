package model;

/**
 *
 * @author tom
 */
public class WriteRequestPacket extends RequestPacket {

	public WriteRequestPacket(String filename, Protocol.Mode mode) {
		super(Protocol.OpCode.WRQ, filename, mode);
	}
}
