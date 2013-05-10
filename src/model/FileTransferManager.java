/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pierre
 */
public class FileTransferManager {

	private int _port;
	private InetAddress _destination;

	public FileTransferManager(InetAddress destination) {
		this._destination = destination;
		this._port = Protocol.PORT;
	}
	//*

	public int sendFile(String filepath, Protocol.Mode mode) {


		try {
			
			//  --- ouverture du fichier --- //
			File file = new File(filepath);
			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			
			// ouverture du socket et création des variables référentes
			byte buffer[] = new byte[Protocol.BUFFER_SIZE];
			byte data[];// = new byte[Protocol.BUFFER_SIZE];
			int port = this._port;
			short block;
			boolean sendBack;
			
			DatagramSocket socket = new DatagramSocket();

			byte packet[] = new WriteRequestPacket(file.getName(), mode).getBytes();
			
			DatagramPacket dpOut = new DatagramPacket(packet, packet.length, this._destination, port);
			DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
			
			System.out.println(packet[0]);
			System.out.println(packet[1]);
			
			// --- Envoie de la demande d'écriture --- //
			socket.setSoTimeout(5000);
			this.send(socket, dpOut, dpIn);
			
			Packet response = PacketFactory.toPacket(dpIn.getData());
			// --- gestion des erreurs --- //
			if(response.getOpCode() == Protocol.OpCode.ERR) {
				//TODO: gestion de l'erreur
				return response.getOpCode().v();
			}
			// --- gestion de l'acquittement --- //
			else if(response.getOpCode() == Protocol.OpCode.ACK) {
				AcknowledgmentPacket ack = (AcknowledgmentPacket)response;
				
				port = dpIn.getPort();
				block = ack.getBlockNumber();
				
				// --- Envoie des données --- //
				int byteCount;
				do {
					// --- Lecture du fichier --- //
					byteCount = stream.available();
					System.out.println(byteCount);
					if(byteCount > 512){
						byteCount = 512;
					}
					data = new byte[byteCount];

					stream.read(data, 0, byteCount);

					packet = new DataPacket((short)(block + 1), data).getBytes();
					dpOut = new DatagramPacket(packet, packet.length, this._destination, port);
					
					// --- Envoie --- //
					sendBack = true;
					do {
						socket.setSoTimeout(500);
						this.send(socket, dpOut, dpIn);
						

						response = PacketFactory.toPacket(dpIn.getData());


						if(response.getOpCode() == Protocol.OpCode.ERR) {
							ErrorPacket ep = (ErrorPacket)response;
							System.out.println(ep.getErrMsg());
							return ep.getErrCode().v();
						}
						else if (response.getOpCode() == Protocol.OpCode.ACK){
							ack = (AcknowledgmentPacket)PacketFactory.toPacket(dpIn.getData());
							if (ack.getBlockNumber() == block+1) {
								sendBack = false;
							}
						}
						

						System.out.println(ack.getBlockNumber());
						System.out.println(block+1);
					} while(sendBack);
					
					block = ack.getBlockNumber();
					System.out.println("\t" + byteCount);
				} while(byteCount == 512);
			}
			System.out.println();
			/*System.out.println(packet[0]);
			System.out.println(packet[1]);
			System.out.println(packet[2]);
			System.out.println(packet[3]);
			System.out.println(packet[4]);
			System.out.println(packet[5]);*/

			System.out.println(dpIn.getData()[0]);
			System.out.println(dpIn.getData()[1]);
			System.out.println(dpIn.getData()[2]);
			System.out.println(dpIn.getData()[3]);
			
			socket.close();
			stream.close();
			
		} catch (SocketTimeoutException e){
			
		} catch (SocketException e) {
			//TODO: log
		} catch (FileNotFoundException ex) {
			Logger.getLogger(FileTransferManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(FileTransferManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		
		
		return 0;

	}
	//*/

	public int receiveFile(String filename, Protocol.Mode mode) {

		// ouvrir fichier

		byte buffer[] = new byte[Protocol.BUFFER_SIZE];

		try {
			DatagramSocket ds = new DatagramSocket();

			byte packet[] = new ReadRequestPacket(filename, mode).getBytes();
			DatagramPacket dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
			DatagramPacket dpIn = new DatagramPacket(buffer, Protocol.BUFFER_SIZE);
			ds.send(dpOut);
			ds.receive(dpIn);
		} catch (SocketException e) {
			//TODO: log
		} catch (IOException e) {
			//TODO: log
		}
		return 0;
	}

	private void send(DatagramSocket socket, DatagramPacket dpOut, DatagramPacket dpIn) throws IOException {
		int sendCounter = 0;
		boolean sendBack;
		do {
			sendBack = false;
			socket.send(dpOut);
			try {
				socket.receive(dpIn);
			}
			catch(SocketTimeoutException exc){
				++sendCounter;
				if(sendCounter > 3){
					throw exc;
				}
				sendBack = true;
			}
		} while(sendBack);
	}
	
	public InetAddress getDestination() {
		return _destination;
	}

	public void setDestination(InetAddress destination) {
		this._destination = destination;
	}
}
