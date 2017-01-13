/**
 * 
 */

/**
 * @author Jussi Wallin, Antti Auranen, Niklas Luomala
 *
 */

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
        

public class Server implements TicTacToe {
	
	public Server () {};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Server srv = new Server();
			TicTacToe stub = (TicTacToe) UnicastRemoteObject.exportObject(srv, 0);

			Registry registry = LocateRegistry.getRegistry(1099);
			registry.bind("TicTacToe", stub);
			
			System.err.println("Debug: Server ready.");
		} catch (Exception e) {
			System.err.println("Debug: Error: "+ e.toString());
			e.printStackTrace();
		}

	}

	@Override
	public int registerPlayer() throws RemoteException {
		System.err.println("Debug: Registered player");
		return -1;
	}

}
