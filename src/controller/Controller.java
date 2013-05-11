/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

/**
 *
 * @author Quentin
 */
public class Controller {
    
        public void send(final String name){
            Thread t = new Thread(new Runnable() {

		@Override
		public void run() {
			//TODO
		}
            });

            t.start();
        }
	
        public void receive(final String name){
            Thread t = new Thread(new Runnable() {

		@Override
		public void run() {
			//TODO
		}
            });

            t.start();
        }
}
