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
import java.net.InetAddress;

/**
 *
 * @author Pierre
 */
public class FileTransferManager {
    
    private InetAddress _destination;
    
    public FileTransferManager(InetAddress destination) {
	_destination = destination;
    }

    public int sendFile() {
	
	try {
	    DataInputStream  br = new DataInputStream (new FileInputStream("multiples.txt"));
	    
	    while(br.readByte()) {
		
	    }
	    
	}
	catch(FileNotFoundException exc) {
	    //TODO: log..
	    return 1;
	}
	
	return 0;
    }
    
    public int receiveFile() {
	return 0;
    }
    
    public InetAddress getDestination() {
	return _destination;
    }

    public void setDestination(InetAddress destination) {
	this._destination = destination;
    }
}
