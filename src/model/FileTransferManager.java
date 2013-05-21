package model;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

	public void sendFile(String filepath, Protocol.Mode mode) {
		FileInputStream stream = null;
		File file = null;
		String message = null;
		try {
			// --- ouverture du fichier --- //
			file = new File(filepath);
			stream = new FileInputStream(file);

			// ouverture du socket et création des variables référentes
			byte buffer[] = new byte[Protocol.BUFFER_SIZE];
			byte data[] = null;
			short block;

			boolean sendBack;

			DatagramSocket socket = new DatagramSocket();
			byte packet[] = new WriteRequestPacket(file.getName(), mode).getBytes();
			DatagramPacket dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
			DatagramPacket dpIn = new DatagramPacket(buffer, buffer.length);

			message = "Transfer of '" + file.getName() + "'.";
			this.setChanged();
			this.notifyObservers(message);

			// --- Envoi de la demande d'écriture --- //
			socket.setSoTimeout(Protocol.SOCKET_TIMEOUT);
			Packet response = this.send(socket, dpOut, dpIn);
			// --- gestion des erreurs --- //
			if (response.getOpCode() == Protocol.OpCode.ERR) {

				ErrorPacket err = (ErrorPacket) response;

				throw new TFTPErrorException(err.getErrCode(), err.getErrMsg());
			} // --- gestion de l'acquittement --- //
			else if (response.getOpCode() == Protocol.OpCode.ACK) {
				AcknowledgmentPacket ack = (AcknowledgmentPacket) response;

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

					if (byteCount > Protocol.DATA_SIZE) {
						byteCount = Protocol.DATA_SIZE;
					}
					data = new byte[byteCount];

					//stream.read(data, 0, byteCount);
					this.read(stream, data, byteCount, mode);

					DataPacket dp = new DataPacket((short) (block + 1), data);
					dp.setMode(mode);
					packet = dp.getBytes();
					dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);

					// --- Envoi --- //
					sendBack = true;
					do {
						socket.setSoTimeout(Protocol.SOCKET_TIMEOUT);
						response = this.send(socket, dpOut, dpIn);

						if (response.getOpCode() == Protocol.OpCode.ERR) {
							ErrorPacket err = (ErrorPacket) response;
							throw new TFTPErrorException(err.getErrCode(), err.getErrMsg());
						} else if (response.getOpCode() == Protocol.OpCode.ACK) {
							ack = (AcknowledgmentPacket) response;
							if (ack.getBlockNumber() == block + 1) {
								sendBack = false;
							}
						}

					} while (sendBack);

					block = ack.getBlockNumber();

				} while (byteCount == Protocol.DATA_SIZE);
			}

			message = "Transfer of '" + file.getName() + "' has successfully completed.";
			socket.close();
		} catch (FileNotFoundException ex) {
			message = "Error : " + ex.getMessage();
		} catch (TFTPErrorException ex) {
			message = "Transfer of '" + file.getName() + "' - Error : " + ex.getErrCode() + " - " + ex.getMessage();
		} catch (Exception ex) {
			message = "Transfer of '" + file.getName() + "' - Error : " + ex.getMessage();
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
			byte data[] = null;
			int dataLength = Protocol.DATA_SIZE;
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
				if (response == null) {
					throw new Exception("Invalid OpCode.");
				}
				if (response.getOpCode() == Protocol.OpCode.ERR) {
					ErrorPacket err = (ErrorPacket) response;
					throw new TFTPErrorException(err.getErrCode(), err.getErrMsg());
				} // --- Réception des données --- //
				else if (response.getOpCode() == Protocol.OpCode.DATA) {
					if (block == 1) {
						message = "Transfer of '" + file.getName() + "' has begun.";
						this.setChanged();
						this.notifyObservers(message);
					}

					this._port = dpIn.getPort();
					DataPacket datap = (DataPacket) response;
					dataLength = dpIn.getLength() - (Protocol.OPCODE_SIZE + Protocol.BLOCKNUM_SIZE);
					if (block == datap.getBlockNumber()) {
						data = datap.getData();

						// --- Écriture du fichier --- //
						stream.write(data, 0, dataLength);
						++block;
					}
					// --- Envoi de l'acquittement --- //
					packet = new AcknowledgmentPacket(block).getBytes();
					dpOut = new DatagramPacket(packet, packet.length, this._destination, this._port);
					socket.send(dpOut);
				}
				System.out.println(data.length);
			} while (dataLength == Protocol.DATA_SIZE);

			message = "Transfer of '" + file.getName() + "' has successfully completed.";
			socket.close();
		} catch (FileNotFoundException ex) {
			message = "Error : " + ex.getMessage();
		} catch (TFTPErrorException ex) {
			message = "Transfer of '" + file.getName() + "' - Error : " + ex.getErrCode() + " - " + ex.getMessage();
		} catch (Exception ex) {
			message = "Transfer of '" + file.getName() + "' - Error : " + ex.getMessage();
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

			try {
				System.out.println("ok");
				socket.receive(dpIn);
				System.out.println("ko");

				packet = PacketFactory.toPacket(dpIn.getData());

				if (packet == null) {
					throw new Exception("Invalid OpCode.");
				}
			} catch (Exception ex) {

				++sendCounter;
				if (sendCounter > Protocol.SEND_COUNTER) {
					throw ex;
				}
				sendBack = true;
			}
		} while (sendBack);

		return packet;
	}

	public InetAddress getDestination() {
		return _destination;
	}

	public void setDestination(InetAddress destination) {
		this._destination = destination;
	}

	public void read(InputStream stream, byte[] data, int byteCount, Protocol.Mode mode) throws IOException {
		if (mode == Protocol.Mode.OCTET) {
			stream.read(data, 0, byteCount);
		} else if (mode == Protocol.Mode.NETASCII) {

			//byte[] buf = new byte[byteCount/2];
			//char[] cbuf = new char[byteCount/2];

			/*stream.read(buf, 0, byteCount/2);
			 data = new String(buf, "UTF-8").getBytes("UTF-8);*/
			stream.read(data, 0, byteCount);
			/*InputStreamReader ipsr=new InputStreamReader(stream);
			 BufferedReader br=new BufferedReader(ipsr);
			 br.read(cbuf, 0, byteCount/2);
			 String str = new String(cbuf);
			 data = str.getBytes("US-ASCII");*/
		}
	}
}
