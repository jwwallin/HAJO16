
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToe extends Remote{
	int registerPlayer() throws RemoteException;
	
}
