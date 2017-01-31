/**
 * 
 */

/**
 * @author Jussi Wallin, Antti Auranen, Niklas Luomala
 *
 */

import java.io.Console;
import java.io.IOException;
import java.rmi.Naming;
        

public class Server {
	
	
	public Server () {};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TicTacToe srv = new TicTacToeSrv();

			Naming.rebind("rmi://localhost:15000/TicTacToe", srv);
			
			System.err.println("Debug: Server ready.");
			
			
		} catch (Exception e) {
			System.err.println("Debug: Error: "+ e.toString());
			e.printStackTrace();
		}
		
		stop();

	}
	
	static void stop() {

		Console c = System.console();
	    if (c != null) {
	        c.format("\nPress ENTER to proceed.\n");
	        c.readLine();
	    }
	}

}

