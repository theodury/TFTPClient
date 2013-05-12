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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pierre
 */
public class FileTransferManager extends Observable {

	public static int SEND_COUNTER = 3;
	
	private int _port;
	private InetAddress _destination;

	public FileTransferManager(InetAddress destination) {
		this._destination = destination;
		this._port = Protocol.PORT;
	}
	//*

	public void sendFile(String filepath, Protocol.Mode mode)  {
		DataInputStream stream = null;
		File file = null;
		try {
			//try {
			//  --- ouverture du fichier --- //
			file = new File(filepath);
			stream = new DataInputStream(new FileInputStream(file));
			
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
			
			String message = "Transfert of '" + file.getName() + "'.";
				this.setChanged();
				this.notifyObservers(message);
			
			// --- Envoie de la demande d'écriture --- //
			socket.setSoTimeout(5000);
			this.send(socket, dpOut, dpIn);
			Packet response = PacketFactory.toPacket(dpIn.getData());
			// --- gestion des erreurs --- //
			if(response.getOpCode() == Protocol.OpCode.ERR) {
				
				ErrorPacket err = (ErrorPacket)response;
				
				throw new TFTPErrorException(err.getErrCode().v(), err.getErrMsg());
			}
			// --- gestion de l'acquittement --- //
			else if(response.getOpCode() == Protocol.OpCode.ACK) {
				AcknowledgmentPacket ack = (AcknowledgmentPacket)response;
				
				port = dpIn.getPort();
				block = ack.getBlockNumber();
				
				message = "Transfert of '" + file.getName() + "' has begun";
				this.setChanged();
				this.notifyObservers(message);
				
				// --- Envoie des données --- //
				int byteCount;
				do {
					// --- Lecture du fichier --- //
					byteCount = stream.available();
					System.out.println(byteCount);
					if(byteCount > Protocol.DATA_SIZE){
						byteCount = Protocol.DATA_SIZE;
					}
					data = new byte[byteCount];

					stream.read(data, 0, byteCount);

					packet = new DataPacket((short)(block + 1), data).getBytes();
					dpOut = new DatagramPacket(packet, packet.length, this._destination, port);
					
					// --- Envoie --- //
					sendBack = true;
					do {
						socket.setSoTimeout(500);
						response = this.send(socket, dpOut, dpIn);
						

						 //PacketFactory.toPacket(dpIn.getData());


						if(response.getOpCode() == Protocol.OpCode.ERR) {
							
							ErrorPacket err = (ErrorPacket)response;

							throw new TFTPErrorException(err.getErrCode().v(), err.getErrMsg());
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
				} while(byteCount == Protocol.DATA_SIZE);
			}
			
			message = "Transfert of '" + file.getName() + "' has successfully completed.";
			
			this.setChanged();
			this.notifyObservers(message);
			
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
			
		} 
		catch (FileNotFoundException ex) {
			String message = "Error : " + ex.getMessage();
			
			System.out.println(file.getPath());
			
			this.setChanged();
			this.notifyObservers(message);
		} 
		catch (IOException ex) {
			
			String message = "Transfert of '"+ file.getName() +"' - Error : " + ex.getMessage();
			
			this.setChanged();
			this.notifyObservers(message);
		} 
		catch (TFTPErrorException ex) {
			
			String message = "Transfert of '"+ file.getName() +"' - Error : " + ex.getErrCode() + " - " + ex.getMessage();
			
			this.setChanged();
			this.notifyObservers(message);
			
		} catch (Exception ex) {
			
			String message = "Transfert of '"+ file.getName() +"' - Error : " + ex.getMessage();
			
			this.setChanged();
			this.notifyObservers(message);
		} finally {
			
			try {
				stream.close();
			} catch (IOException ex) {
				//Logger.getLogger(FileTransferManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

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

	private Packet send(DatagramSocket socket, DatagramPacket dpOut, DatagramPacket dpIn) throws IOException, Exception {
		int sendCounter = 0;
		Packet packet = null;
		boolean sendBack;
		do {
			sendBack = false;
			socket.send(dpOut);
			System.out.println(socket.getPort());
			try {
				socket.receive(dpIn);
				
				packet = PacketFactory.toPacket(dpIn.getData());
				
				if(packet == null) {
					throw new Exception("Invalid OpCode.");
				}
			} 
			catch (Exception ex) {
				
				++sendCounter;
				if(sendCounter > FileTransferManager.SEND_COUNTER){
					throw ex;
				}
				sendBack = true;
			}
		} while(sendBack);
		
		return packet;
	}
	
	public InetAddress getDestination() {
		return _destination;
	}

	public void setDestination(InetAddress destination) {
		this._destination = destination;
	}
}
