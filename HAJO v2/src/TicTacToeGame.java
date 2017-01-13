/**
 * 
 */

/**
 * @author Jussi Walli, Antti Auranen, Niklas Niemelä
 *
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class TicTacToeGame {
	
	private TicTacToeGame() {}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            TicTacToe stub = (TicTacToe) registry.lookup("TicTacToe");
            int response = stub.registerPlayer();
            System.out.println("Player number: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

	}

}
