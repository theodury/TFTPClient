package tftpclient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.*;

/**
 *
 * @author p1207814
 */
public class TFTPClient {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		/*
		ByteBuffer buffer = ByteBuffer.allocate(3);
		//buffer.order(ByteOrder.BIG_ENDIAN);
		
		buffer.put((byte)1);
		buffer.put((byte)2);
		
		System.out.println(buffer.getShort(0));
		
		byte[] bytes = buffer.array();
		
		for (byte b : bytes) {
		   System.out.format("0x%x ", b);
		   System.out.println(b);
		}
		//*/
		//*
		try {
			InetAddress destination = InetAddress.getLocalHost();
			
			FileTransferManager ftm = new FileTransferManager(destination);
			
			ftm.sendFile("D:\\Mes Documents\\Mes Images\\Wallpapers\\ba47dd5d51587985afdffdfdb50ba1e1.png", Protocol.Mode.NETASCII);
			//ftm.sendFile("D:\\Mes Documents\\Mes Images\\NiarK.bmp", Protocol.Mode.NETASCII);
			//ftm.sendFile("D:\\Mes Documents\\GitHub\\TFTPFile\\client\\test2.txt", Protocol.Mode.OCTET);
			
		} catch (UnknownHostException ex) {
			Logger.getLogger(TFTPClient.class.getName()).log(Level.SEVERE, null, ex);
		}
		//*/
		
	}
}
