/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Observable;

/**
 *
 * @author Pierre
 */
public class FileTransferManager extends Observable {
	
	private int _port;
	private InetAddress _destination;

	public FileTransferManager(InetAddress destination) {
		this._destination = destination;
		this._port = Protocol.PORT;
	}

	public void sendFile(String filepath, Protocol.Mode mode)  {
		DataInputStream stream = null;
		File file = null;
		String message = null;
		try {
			// --- ouverture du fichier --- //
			file = new File(filepath);
			stream = new DataInputStream(new FileInputStream(file));
			
			// --- ouverture du socket et création des variables référentes --- //
			byte buffer[] = new byte[Protocol.BUFFER_SIZE];
			byte data[] = null;
			short block;
			
			boolean sendBack;
			
			DatagramSocket socket = new DatagramSocket();
			byte packet[] = new WriteRequestPacket(file.getName(), mode).getBytes();
			DatagramPacket dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
			DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
//			System.out.println(packet[0]);
//			System.out.println(packet[1]);
			
			message = "Transfer of '" + file.getName() + "'.";
			this.setChanged();
			this.notifyObservers(message);
			
			// --- Envoi de la demande d'écriture --- //
			socket.setSoTimeout(Protocol.SOCKET_TIMEOUT);
			this.send(socket, dpOut, dpIn);
			Packet response = PacketFactory.toPacket(dpIn.getData());
			// --- Gestion des erreurs --- //
			if(response.getOpCode() == Protocol.OpCode.ERR) {
				
				ErrorPacket err = (ErrorPacket)response;
				
				throw new TFTPErrorException(err.getErrCode(), err.getErrMsg());
			} // --- Gestion de l'acquittement --- //
			else if(response.getOpCode() == Protocol.OpCode.ACK) {
				AcknowledgmentPacket ack = (AcknowledgmentPacket)response;
				
				this._port = dpIn.getPort();
				block = ack.getBlockNumber();
				
				message = "Transfer of '" + file.getName() + "' has begun.";
				this.setChanged();
				this.notifyObservers(message);
				
				// --- Envoi des données --- //
				int byteCount;
				do {
					// --- Lecture du fichier --- //
					byteCount = stream.available();
//					System.out.println(byteCount);
					if(byteCount > Protocol.DATA_SIZE) {
						byteCount = Protocol.DATA_SIZE;
					}
					data = new byte[byteCount];

					stream.read(data, 0, byteCount);

					packet = new DataPacket((short)(block + 1), data).getBytes();
					dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
					
					// --- Envoi --- //
					sendBack = true;
					do {
						socket.setSoTimeout(Protocol.SOCKET_TIMEOUT);
						response = this.send(socket, dpOut, dpIn);
						//PacketFactory.toPacket(dpIn.getData());

						if(response.getOpCode() == Protocol.OpCode.ERR) {
							ErrorPacket err = (ErrorPacket)response;
							throw new TFTPErrorException(err.getErrCode(), err.getErrMsg());
						}
						else if (response.getOpCode() == Protocol.OpCode.ACK){
							ack = (AcknowledgmentPacket)PacketFactory.toPacket(dpIn.getData());
							if (ack.getBlockNumber() == block+1) {
								sendBack = false;
							}
						}
						
//						System.out.println(ack.getBlockNumber());
//						System.out.println(block+1);
					} while(sendBack);
					
					block = ack.getBlockNumber();
//					System.out.println("\t" + byteCount);
				} while(byteCount == Protocol.DATA_SIZE);
			}
			
			message = "Transfer of '" + file.getName() + "' has successfully completed.";
			/*
			this.setChanged();
			this.notifyObservers(message);
			System.out.println();
			System.out.println(packet[0]);
			System.out.println(packet[1]);
			System.out.println(packet[2]);
			System.out.println(packet[3]);
			System.out.println(packet[4]);
			System.out.println(packet[5]);
			System.out.println(dpIn.getData()[0]);
			System.out.println(dpIn.getData()[1]);
			System.out.println(dpIn.getData()[2]);
			System.out.println(dpIn.getData()[3]);
			//*/
			socket.close();
		} catch (FileNotFoundException ex) {
			message = "Error : " + ex.getMessage();
		} catch (TFTPErrorException ex) {
			message = "Transfer of '"+ file.getName() +"' - Error : " + ex.getErrCode() + " - " + ex.getMessage();
		} catch (Exception ex) {
			message = "Transfer of '"+ file.getName() +"' - Error : " + ex.getMessage();
		} finally {
			this.setChanged();
			this.notifyObservers(message);
			try {
				stream.close();
			} catch (IOException ex) {
				//Logger.getLogger(FileTransferManager.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public int receiveFile(String localFilePath, String serverFilename, Protocol.Mode mode) {
		DataOutputStream stream = null;
		File file = null;
		String message = null;
		try {
			// --- Ouverture du fichier --- //
			file = new File(localFilePath);
			stream = new DataOutputStream(new FileOutputStream(file));

			// --- Ouverture du socket et création des variables référentes --- //
			byte buffer[] = new byte[Protocol.BUFFER_SIZE];
			System.out.println("receiveFile (BUFFER_SIZE) :" + Protocol.BUFFER_SIZE);
			byte data[] = null;
			short block = 1;
		
			DatagramSocket socket = new DatagramSocket();
			byte packet[] = new ReadRequestPacket(serverFilename, mode).getBytes();
			DatagramPacket dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
			DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);
			
			message = "Transfer of '" + file.getName() + "'.";
			this.setChanged();
			this.notifyObservers(message);

			// --- Envoi de la demande de lecture --- //
			socket.setSoTimeout(Protocol.SOCKET_TIMEOUT);
			socket.send(dpOut);
			
			do {
				socket.receive(dpIn);
				Packet response = PacketFactory.toPacket(dpIn.getData());
				// --- Gestion des erreurs --- //
				if(response == null) {
					throw new Exception("Invalid OpCode.");
				}
				if(response.getOpCode() == Protocol.OpCode.ERR) {
					ErrorPacket err = (ErrorPacket)response;
					throw new TFTPErrorException(err.getErrCode(), err.getErrMsg());
				} // --- Réception des données --- //
				else if(response.getOpCode() == Protocol.OpCode.DATA) {
					message = "Transfer of '" + file.getName() + "' has begun.";
					this.setChanged();
					this.notifyObservers(message);
					
					this._port = dpIn.getPort();
					DataPacket datap = (DataPacket)response;
					System.out.println("block = " + block + "\tgetBlockNumber() = " + datap.getBlockNumber());
					if(block == datap.getBlockNumber()) {
						++block;
						data = datap.getData();

						// --- Écriture du fichier --- //
						stream.write(data);

						// --- Envoi de l'acquittement --- //
						packet = new AcknowledgmentPacket(block).getBytes();
						dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
						socket.send(dpOut);
					}
				}
				System.out.println("receiveFile (data.length) :" + data.length);
			} while(data.length == Protocol.DATA_SIZE);
			
			message = "Transfer of '" + file.getName() + "' has successfully completed.";
			socket.close();
		} catch(FileNotFoundException ex) {
			message = "Error : " + ex.getMessage();
		} catch (TFTPErrorException ex) {
			message = "Transfer of '"+ file.getName() +"' - Error : " + ex.getErrCode() + " - " + ex.getMessage();
		} catch (Exception ex) {
			message = "Transfer of '"+ file.getName() +"' - Error : " + ex.getMessage();
		} finally {
			this.setChanged();
			this.notifyObservers(message);
			try {
				stream.close();
			} catch (IOException ex) {
				//Logger.getLogger(FileTransferManager.class.getName()).log(Level.SEVERE, null, ex);
			}
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
//			System.out.println(socket.getPort());
			try {
				socket.receive(dpIn);
				
				packet = PacketFactory.toPacket(dpIn.getData());
				
				if(packet == null) {
					throw new Exception("Invalid OpCode.");
				}
			} 
			catch (Exception ex) {
				
				++sendCounter;
				if(sendCounter > Protocol.SEND_COUNTER){
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
