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
			System.out.println("factory (buffer.length) : " + buffer.length);
			short block = Packet.getCode(buffer[2], buffer[3]);//buffer[2] * 256 + buffer[3];
			
			packet = new DataPacket(block, Arrays.copyOfRange(buffer, 4, buffer.length));
		}
		else if(opCode == Protocol.OpCode.ACK.v()) {
			
			short block = Packet.getCode(buffer[2], buffer[3]);//buffer[2] * 256 + buffer[3];
			
			packet = new AcknowledgmentPacket(block);
		}
		else if(opCode == Protocol.OpCode.ERR.v()) {
			
			Protocol.ErrCode errorCode = Protocol.ErrCode.values()[Packet.getCode(buffer[2], buffer[3])];
			
			StringBuilder message = new StringBuilder();
			for( int i = 4; i< buffer.length && buffer[i] != 0; ++i) {
				message.append((char)(buffer[i]));
				
			}
			
			packet = new ErrorPacket(errorCode, message.toString());
		}
		
		return packet;
	}
}
