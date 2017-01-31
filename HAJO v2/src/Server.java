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
			TicTacToe srv = new TicTacToeSrv();

			Naming.rebind("rmi://localhost:15000/TicTacToe", srv);
			
			System.err.println("Debug: Server ready.");
			
			
		} catch (Exception e) {
			System.err.println("Debug: Error: "+ e.toString());
			e.printStackTrace();
		}
	}
}

