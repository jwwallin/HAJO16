/**
 * @author Jussi Wallin, Antti Auranen, Niklas Luomala
 *
 */

import java.rmi.Naming;


public class Server {
		
	public Server () {};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Creates the server object
			TicTacToe srv = new TicTacToeSrv();
			// Name the RMI call using java.rmi.Naming library
			Naming.rebind("rmi://localhost:15000/TicTacToe", srv);
			// Print message to server console output
			System.err.println("Debug: Server ready.");
			
		} catch (Exception e) {
			// Print error message to server console output 
			System.err.println("Debug: Error: "+ e.toString());
			e.printStackTrace();
		}
	}
}

