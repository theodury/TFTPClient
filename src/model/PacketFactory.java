/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Arrays;

/**
 *
 * @author Pierre
 */
public class PacketFactory {
	
	public PacketFactory(){
		
	}
	
	public static Packet toPacket(byte[] buffer) {
		
		Packet packet = null;
		
		int opCode = Packet.getCode(buffer[0], buffer[1]); //buffer[0] * 256 + buffer[1];
		
		if(opCode == Protocol.OpCode.RRQ.v()) {
			// Flemme de faire ça tout de suite
		}
		else if(opCode == Protocol.OpCode.WRQ.v()) {
			
			// Flemme de faire ça tout de suite
		}
		else if(opCode == Protocol.OpCode.DATA.v()) {
			
			short block = Packet.getCode(buffer[2], buffer[3]);//buffer[2] * 256 + buffer[3];
			
			packet = new DataPacket(block, Arrays.copyOfRange(buffer, 4, buffer.length));
		}
		else if(opCode == Protocol.OpCode.ACK.v()) {
			
			short block = Packet.getCode(buffer[2], buffer[3]);//buffer[2] * 256 + buffer[3];
			
			packet = new AcknowledgmentPacket(block);
		}
		else if(opCode == Protocol.OpCode.ERR.v()) {
			
			Protocol.ErrCode errorCode = Protocol.ErrCode.values()[Packet.getCode(buffer[2], buffer[3])];
			
			String message = new String(Arrays.copyOfRange(buffer, 4, buffer.length - 1));
			
			packet = new ErrorPacket(errorCode, message);
		}
		
		return packet;
	}
}
