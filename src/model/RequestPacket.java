/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author tom
 */
abstract public class RequestPacket extends Packet {

	protected String _filename;
	protected Protocol.Mode _mode;

	public RequestPacket(Protocol.OpCode opCode, String filename, Protocol.Mode mode) {
		super(opCode);
		this._filename = filename;
		this._mode = mode;
	}

	@Override
	public byte[] getBytes() {
		StringBuilder packet = new StringBuilder();
		packet.append(intToEscStr(this._opCode.v(), Protocol.OPCODE_SIZE));
		packet.append(this._filename);
		packet.append(intToEscStr(0, 1));
		packet.append(this._mode.v());
		packet.append(intToEscStr(0, 1));
		return packet.toString().getBytes();
	}

	public String getFilename() {
		return _filename;
	}

	public void setFilename(String filename) {
		this._filename = filename;
	}

	public Protocol.Mode getMode() {
		return _mode;
	}

	public void setMode(Protocol.Mode mode) {
		this._mode = mode;
	}
}
