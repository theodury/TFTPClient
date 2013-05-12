/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FileTransferManager;
import model.Protocol;

/**
 *
 * @author Quentin
 */
public class Controller {
	
	Observer _obs;

	public Controller(Observer obs) {
		_obs = obs;
	}

	public void send(final String pathName, String destinationAddress) {


		try {
			final FileTransferManager ftm = new FileTransferManager(InetAddress.getByName(destinationAddress));
			
			ftm.addObserver(_obs);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					
					ftm.sendFile(pathName, Protocol.Mode.NETASCII);
					
				}
			});

			t.start();
			
		} catch (UnknownHostException ex) {
			Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void receive(final String name) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				//TODO
			}
		});

		t.start();
	}
}
