/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
	/*
	 public int sendFile() {

	 try {
	 DataInputStream br = new DataInputStream(new FileInputStream("multiples.txt"));

	 while (br.readByte()) {
	 }

	 } catch (FileNotFoundException exc) {
	 //TODO: log..
	 return 1;
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

	public InetAddress getDestination() {
		return _destination;
	}

	public void setDestination(InetAddress destination) {
		this._destination = destination;
	}
}
