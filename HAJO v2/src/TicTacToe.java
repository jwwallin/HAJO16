
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TicTacToe extends Remote{
	
	int registerPlayer() throws Exception;
	int[] getBoardState() throws Exception;
	boolean getTurn(int playerNum) throws Exception;
	int doTurn(int playerNum, int pos) throws Exception;
	boolean gameGoing() throws Exception;
	boolean startGame(int playerNum) throws Exception;
	
}
